package android.app.faunadex.data.repository

import android.app.faunadex.domain.model.Question
import android.app.faunadex.domain.model.Quiz
import android.app.faunadex.domain.model.QuizAttempt
import android.app.faunadex.domain.model.UserAnswer
import android.app.faunadex.domain.repository.QuizRepository
import android.app.faunadex.domain.repository.QuizStats
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : QuizRepository {

    private val quizzesCollection = firestore.collection("quizzes")
    private val questionsCollection = firestore.collection("questions")
    private val attemptsCollection = firestore.collection("quiz_attempts")


    override suspend fun getQuizzes(educationLevel: String?): Result<List<Quiz>> {
        return try {
            val query = if (educationLevel != null) {
                quizzesCollection
                    .whereEqualTo("is_active", true)
                    .whereEqualTo("education_level", educationLevel)
            } else {
                quizzesCollection.whereEqualTo("is_active", true)
            }

            val snapshot = query.get().await()
            val quizzes = snapshot.documents.mapNotNull { doc ->
                parseQuizFromDocument(doc)
            }

            android.util.Log.d("QuizRepository", "Loaded ${quizzes.size} quizzes for education level: $educationLevel")
            quizzes.forEach { quiz ->
                android.util.Log.d("QuizRepository", "  - Quiz: ${quiz.titleEn}, totalQuestions=${quiz.totalQuestions}, level=${quiz.educationLevel}")
            }

            Result.success(quizzes)
        } catch (e: Exception) {
            android.util.Log.e("QuizRepository", "Error loading quizzes: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getQuizById(quizId: String): Result<Quiz> {
        return try {
            val document = quizzesCollection.document(quizId).get().await()

            if (!document.exists()) {
                return Result.failure(Exception("Quiz not found"))
            }

            val quiz = parseQuizFromDocument(document)
                ?: return Result.failure(Exception("Failed to parse quiz"))

            android.util.Log.d("QuizRepository", "Loaded quiz: id=${quiz.id}, title=${quiz.titleEn}, totalQuestions=${quiz.totalQuestions}, educationLevel=${quiz.educationLevel}")

            Result.success(quiz)
        } catch (e: Exception) {
            android.util.Log.e("QuizRepository", "Error loading quiz: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getQuizzesByCategory(category: String): Result<List<Quiz>> {
        return try {
            val snapshot = quizzesCollection
                .whereEqualTo("category", category)
                .whereEqualTo("is_active", true)
                .get()
                .await()

            val quizzes = snapshot.documents.mapNotNull { doc ->
                parseQuizFromDocument(doc)
            }
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuestionsByQuizId(quizId: String): Result<List<Question>> {
        return try {
            val snapshot = questionsCollection
                .whereEqualTo("quiz_id", quizId)
                .orderBy("order_index")
                .get()
                .await()

            val questions = snapshot.documents.mapNotNull { doc ->
                parseQuestionFromDocument(doc)
            }

            android.util.Log.d("QuizRepository", "getQuestionsByQuizId($quizId) returned ${questions.size} questions")
            questions.forEach { q ->
                android.util.Log.d("QuizRepository", "  - Question: ${q.id} (order: ${q.orderIndex})")
            }

            Result.success(questions)
        } catch (e: Exception) {
            android.util.Log.e("QuizRepository", "Error getting questions for quiz $quizId with orderBy: ${e.message}")

            // Fallback: get all questions without orderBy and sort in memory
            return try {
                val snapshot = questionsCollection
                    .whereEqualTo("quiz_id", quizId)
                    .get()
                    .await()

                val questions = snapshot.documents.mapNotNull { doc ->
                    parseQuestionFromDocument(doc)
                }.sortedBy { it.orderIndex }

                android.util.Log.d("QuizRepository", "getQuestionsByQuizId($quizId) fallback returned ${questions.size} questions")
                questions.forEach { q ->
                    android.util.Log.d("QuizRepository", "  - Question: ${q.id} (order: ${q.orderIndex})")
                }

                Result.success(questions)
            } catch (fallbackE: Exception) {
                android.util.Log.e("QuizRepository", "Error getting questions for quiz $quizId (fallback also failed): ${fallbackE.message}")
                Result.failure(fallbackE)
            }
        }
    }

    override suspend fun getQuestionById(questionId: String): Result<Question> {
        return try {
            val document = questionsCollection.document(questionId).get().await()

            if (!document.exists()) {
                return Result.failure(Exception("Question not found"))
            }

            val question = parseQuestionFromDocument(document)
                ?: return Result.failure(Exception("Failed to parse question"))

            Result.success(question)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startQuizAttempt(userId: String, quizId: String): Result<QuizAttempt> {
        return try {
            val quiz = getQuizById(quizId).getOrNull()
                ?: return Result.failure(Exception("Quiz not found"))

            val attemptId = UUID.randomUUID().toString()
            val attempt = QuizAttempt(
                id = attemptId,
                userId = userId,
                quizId = quizId,
                totalQuestions = quiz.totalQuestions,
                isCompleted = false,
                startedAt = Date()
            )

            val attemptData = mapOf(
                "user_id" to attempt.userId,
                "quiz_id" to attempt.quizId,
                "score" to attempt.score,
                "total_questions" to attempt.totalQuestions,
                "correct_answers" to attempt.correctAnswers,
                "wrong_answers" to attempt.wrongAnswers,
                "completion_percentage" to attempt.completionPercentage,
                "xp_earned" to attempt.xpEarned,
                "time_taken_seconds" to attempt.timeTakenSeconds,
                "answers" to attempt.answers,
                "is_completed" to false,
                "started_at" to attempt.startedAt,
                "completed_at" to attempt.completedAt
            )

            attemptsCollection.document(attemptId).set(attemptData).await()

            Result.success(attempt)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitQuizAttempt(attempt: QuizAttempt): Result<Unit> {
        return try {
            android.util.Log.d("QuizRepository", "submitQuizAttempt called")
            android.util.Log.d("QuizRepository", "  - attemptId: ${attempt.id}")
            android.util.Log.d("QuizRepository", "  - userId: ${attempt.userId}")
            android.util.Log.d("QuizRepository", "  - quizId: ${attempt.quizId}")
            android.util.Log.d("QuizRepository", "  - isCompleted: ${attempt.isCompleted}")
            android.util.Log.d("QuizRepository", "  - score: ${attempt.score}")

            // Validate attemptId is not empty
            if (attempt.id.isBlank()) {
                android.util.Log.e("QuizRepository", "Error: attemptId is empty or blank")
                return Result.failure(Exception("Invalid quiz attempt: missing attempt ID"))
            }

            val completedAttempt = attempt.copy(
                isCompleted = true,
                completedAt = Date()
            )

            // Create a Map with all fields explicitly using snake_case keys
            val attemptData = mapOf(
                "user_id" to completedAttempt.userId,
                "quiz_id" to completedAttempt.quizId,
                "score" to completedAttempt.score,
                "total_questions" to completedAttempt.totalQuestions,
                "correct_answers" to completedAttempt.correctAnswers,
                "wrong_answers" to completedAttempt.wrongAnswers,
                "completion_percentage" to completedAttempt.completionPercentage,
                "xp_earned" to completedAttempt.xpEarned,
                "time_taken_seconds" to completedAttempt.timeTakenSeconds,
                "answers" to completedAttempt.answers,
                "is_completed" to true,  // Explicitly set to true
                "started_at" to completedAttempt.startedAt,
                "completed_at" to completedAttempt.completedAt
            )

            android.util.Log.d("QuizRepository", "Saving attempt data with keys: ${attemptData.keys}")
            android.util.Log.d("QuizRepository", "user_id value: ${attemptData["user_id"]}")
            android.util.Log.d("QuizRepository", "is_completed value: ${attemptData["is_completed"]}")

            attemptsCollection.document(attempt.id)
                .set(attemptData)
                .await()

            android.util.Log.d("QuizRepository", "submitQuizAttempt saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("QuizRepository", "Error submitting quiz attempt: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getUserQuizAttempts(userId: String, quizId: String?): Result<List<QuizAttempt>> {
        return try {
            val query = if (quizId != null) {
                attemptsCollection
                    .whereEqualTo("user_id", userId)
                    .whereEqualTo("quiz_id", quizId)
            } else {
                attemptsCollection.whereEqualTo("user_id", userId)
            }

            val snapshot = query
                .orderBy("started_at", Query.Direction.DESCENDING)
                .get()
                .await()

            val attempts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizAttempt::class.java)?.copy(id = doc.id)
            }
            Result.success(attempts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserCompletedQuizIds(userId: String): Result<List<String>> {
        return try {
            android.util.Log.d("QuizRepository", "getUserCompletedQuizIds called for userId: $userId")

            // First, let's get ALL attempts for this user to debug
            val allAttemptsSnapshot = attemptsCollection
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            android.util.Log.d("QuizRepository", "Total attempts for user: ${allAttemptsSnapshot.documents.size}")
            allAttemptsSnapshot.documents.forEach { doc ->
                android.util.Log.d("QuizRepository", "  Document ${doc.id}:")
                android.util.Log.d("QuizRepository", "    - user_id: ${doc.getString("user_id")}")
                android.util.Log.d("QuizRepository", "    - quiz_id: ${doc.getString("quiz_id")}")
                android.util.Log.d("QuizRepository", "    - is_completed: ${doc.getBoolean("is_completed")}")
            }

            // Now do the filtered query
            val snapshot = attemptsCollection
                .whereEqualTo("user_id", userId)
                .whereEqualTo("is_completed", true)
                .get()
                .await()

            android.util.Log.d("QuizRepository", "Filtered query (is_completed=true) returned ${snapshot.documents.size} completed attempts")

            val completedQuizIds = snapshot.documents.mapNotNull { doc ->
                android.util.Log.d("QuizRepository", "Processing document: ${doc.id}")
                val attempt = parseQuizAttemptFromDocument(doc)
                android.util.Log.d("QuizRepository", "Attempt: $attempt, quizId: ${attempt?.quizId}")
                attempt?.quizId
            }.distinct()

            android.util.Log.d("QuizRepository", "Final completed quiz IDs: $completedQuizIds")
            Result.success(completedQuizIds)
        } catch (e: Exception) {
            android.util.Log.e("QuizRepository", "Error in getUserCompletedQuizIds: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getUserQuizStats(userId: String): Result<QuizStats> {
        return try {
            val snapshot = attemptsCollection
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            val attempts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizAttempt::class.java)?.copy(id = doc.id)
            }
            val completed = attempts.filter { it.isCompleted }

            val stats = QuizStats(
                totalQuizzesTaken = attempts.size,
                totalQuizzesCompleted = completed.size,
                totalXpEarned = completed.sumOf { it.xpEarned },
                averageScore = if (completed.isNotEmpty()) {
                    completed.map { it.score }.average()
                } else 0.0,
                bestScore = completed.maxOfOrNull { it.score } ?: 0
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseQuizFromDocument(doc: com.google.firebase.firestore.DocumentSnapshot): Quiz? {
        return try {
            Quiz(
                id = doc.id,
                titleEn = doc.getString("title_en") ?: "",
                titleId = doc.getString("title_id") ?: "",
                imageUrl = doc.getString("image_url") ?: "",
                shortDescriptionEn = doc.getString("short_description_en") ?: "",
                shortDescriptionId = doc.getString("short_description_id") ?: "",
                descriptionEn = doc.getString("description_en") ?: "",
                descriptionId = doc.getString("description_id") ?: "",
                totalQuestions = (doc.get("total_questions") as? Long)?.toInt() ?: 0,
                educationLevel = doc.getString("education_level") ?: "",
                category = doc.getString("category") ?: "",
                difficulty = doc.getString("difficulty") ?: "medium",
                xpReward = (doc.get("xp_reward") as? Long)?.toInt() ?: 100,
                timeLimitSeconds = (doc.get("time_limit_seconds") as? Long)?.toInt() ?: 30,
                questionIds = (doc.get("question_ids") as? List<String>) ?: emptyList(),
                isActive = doc.getBoolean("is_active") ?: true,
                createdAt = doc.getDate("created_at")
            )
        } catch (e: Exception) {
            android.util.Log.e("QuizRepository", "Error parsing quiz document ${doc.id}: ${e.message}")
            null
        }
    }

    private fun parseQuestionFromDocument(doc: com.google.firebase.firestore.DocumentSnapshot): Question? {
        return try {
            Question(
                id = doc.id,
                quizId = doc.getString("quiz_id") ?: "",
                questionTextEn = doc.getString("question_text_en") ?: "",
                questionTextId = doc.getString("question_text_id") ?: "",
                questionType = doc.getString("question_type") ?: "multiple_choice",
                optionsEn = (doc.get("options_en") as? List<String>) ?: emptyList(),
                optionsId = (doc.get("options_id") as? List<String>) ?: emptyList(),
                correctAnswerIndex = (doc.get("correct_answer_index") as? Long)?.toInt() ?: 0,
                explanationEn = doc.getString("explanation_en") ?: "",
                explanationId = doc.getString("explanation_id") ?: "",
                difficulty = doc.getString("difficulty") ?: "medium",
                orderIndex = (doc.get("order_index") as? Long)?.toInt() ?: 0
            )
        } catch (e: Exception) {
            android.util.Log.e("QuizRepository", "Error parsing question document ${doc.id}: ${e.message}")
            null
        }
    }

    private fun parseQuizAttemptFromDocument(doc: com.google.firebase.firestore.DocumentSnapshot): QuizAttempt? {
        return try {
            @Suppress("UNCHECKED_CAST")
            val answersMap = (doc.get("answers") as? Map<String, Map<String, Any>>)?.mapValues { (_, value) ->
                UserAnswer(
                    questionId = value["question_id"] as? String ?: "",
                    selectedAnswerIndex = ((value["selected_answer_index"] as? Number)?.toInt()) ?: -1,
                    isCorrect = value["is_correct"] as? Boolean ?: false,
                    timeTakenSeconds = ((value["time_taken_seconds"] as? Number)?.toInt()) ?: 0
                )
            } ?: emptyMap()

            QuizAttempt(
                id = doc.id,
                userId = doc.getString("user_id") ?: "",
                quizId = doc.getString("quiz_id") ?: "",
                score = (doc.get("score") as? Long)?.toInt() ?: 0,
                totalQuestions = (doc.get("total_questions") as? Long)?.toInt() ?: 0,
                correctAnswers = (doc.get("correct_answers") as? Long)?.toInt() ?: 0,
                wrongAnswers = (doc.get("wrong_answers") as? Long)?.toInt() ?: 0,
                completionPercentage = (doc.get("completion_percentage") as? Long)?.toInt() ?: 0,
                xpEarned = (doc.get("xp_earned") as? Long)?.toInt() ?: 0,
                timeTakenSeconds = (doc.get("time_taken_seconds") as? Long)?.toInt() ?: 0,
                answers = answersMap,
                isCompleted = doc.getBoolean("is_completed") ?: false,
                startedAt = doc.getDate("started_at"),
                completedAt = doc.getDate("completed_at")
            )
        } catch (e: Exception) {
            android.util.Log.e("QuizRepository", "Error parsing quiz attempt document ${doc.id}: ${e.message}")
            null
        }
    }
}
