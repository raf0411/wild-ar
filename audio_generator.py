import os
import json
import shutil
from pathlib import Path
import firebase_admin
from firebase_admin import credentials, firestore
import requests
from gtts import gTTS
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

SUPABASE_URL = os.getenv("SUPABASE_URL", "").rstrip('/') + '/'
SUPABASE_KEY = os.getenv("SUPABASE_KEY")
SUPABASE_BUCKET = os.getenv("SUPABASE_BUCKET", "wildar-audio")

# TTS Configuration - Support multiple languages
TTS_LANGUAGES = {
    'en': 'en',  # English
    'id': 'id'   # Indonesian
}
TTS_SLOW = False  # Set to True for slower speech

SERVICE_ACCOUNT_KEY = "serviceAccountKey.json"

def init_firebase():
    """Initialize Firebase Admin SDK"""
    if not firebase_admin._apps:
        cred = credentials.Certificate(SERVICE_ACCOUNT_KEY)
        firebase_admin.initialize_app(cred)
    return firestore.client()


def generate_audio_gtts(text, output_path, language='en'):
    """Generate audio using Google Text-to-Speech (gTTS)"""
    try:
        tts = gTTS(text=text, lang=language, slow=TTS_SLOW)
        tts.save(output_path)
        print(f"✓ Audio generated ({language}): {output_path}")
        return True
    except Exception as e:
        print(f"✗ Failed to generate audio ({language}): {e}")
        return False

def upload_to_supabase(local_path, storage_path):
    """Upload file to Supabase Storage using REST API"""
    try:
        with open(local_path, 'rb') as f:
            file_content = f.read()

        # Upload to Supabase Storage using REST API
        upload_url = f"{SUPABASE_URL}storage/v1/object/{SUPABASE_BUCKET}/{storage_path}"

        headers = {
            "Authorization": f"Bearer {SUPABASE_KEY}",
            "Content-Type": "audio/mpeg",
        }

        response = requests.post(upload_url, data=file_content, headers=headers)

        if response.status_code in [200, 201]:
            # Get public URL
            public_url = f"{SUPABASE_URL}storage/v1/object/public/{SUPABASE_BUCKET}/{storage_path}"
            print(f"✓ Uploaded to Supabase: {public_url}")
            return public_url
        else:
            print(f"✗ Failed to upload to Supabase: {response.status_code} - {response.text}")
            return None

    except Exception as e:
        print(f"✗ Failed to upload to Supabase: {e}")
        return None

def update_firestore_audio_url(animal_id, audio_urls):
    """Update animal document in Firestore with audio URLs

    Args:
        animal_id: The document ID
        audio_urls: Dictionary with keys like '     ', 'audio_url_id', etc.
    """
    db = init_firebase()
    animal_ref = db.collection('animals').document(animal_id)

    animal_ref.update(audio_urls)
    print(f"✓ Updated Firestore for animal: {animal_id}")
    print(f"  Fields updated: {', '.join(audio_urls.keys())}")

def process_animal(animal_data, temp_dir):
    """Process a single animal: generate audio for both languages and upload"""
    animal_id = animal_data['id']
    animal_name = animal_data.get('name', 'Unknown')

    # Get descriptions in both languages
    description_en = animal_data.get('long_description_en', '')
    description_id = animal_data.get('long_description_id', '')

    print(f"\n{'='*60}")
    print(f"Processing: {animal_name} (ID: {animal_id})")
    print(f"{'='*60}")

    if not description_en and not description_id:
        print("✗ No descriptions found in any language, skipping...")
        return False

    audio_urls = {}

    # Process English description
    if description_en:
        print(f"Generating English description audio...")
        desc_filename_en = f"{animal_id}_description_en.mp3"
        desc_local_path_en = temp_dir / desc_filename_en

        if generate_audio_gtts(description_en, desc_local_path_en, language='en'):
            desc_storage_path_en = f"audio/descriptions/{desc_filename_en}"
            desc_url_en = upload_to_supabase(desc_local_path_en, desc_storage_path_en)
            if desc_url_en:
                audio_urls['audio_url_en'] = desc_url_en
    else:
        print("⚠ No English description found, skipping...")

    # Process Indonesian description
    if description_id:
        print(f"Generating Indonesian description audio...")
        desc_filename_id = f"{animal_id}_description_id.mp3"
        desc_local_path_id = temp_dir / desc_filename_id

        if generate_audio_gtts(description_id, desc_local_path_id, language='id'):
            desc_storage_path_id = f"audio/descriptions/{desc_filename_id}"
            desc_url_id = upload_to_supabase(desc_local_path_id, desc_storage_path_id)
            if desc_url_id:
                audio_urls['audio_url_id'] = desc_url_id
    else:
        print("⚠ No Indonesian description found, skipping...")

    # Update Firestore if we generated any audio
    if audio_urls:
        update_firestore_audio_url(animal_id, audio_urls)
        return True

    print("✗ No audio generated for this animal")
    return False

def main():
    """Main function to process all animals"""
    required_vars = [
        "SUPABASE_URL",
        "SUPABASE_KEY",
        "SUPABASE_BUCKET"
    ]

    missing_vars = [var for var in required_vars if not os.getenv(var)]
    if missing_vars:
        print(f"✗ Missing environment variables: {', '.join(missing_vars)}")
        return

    temp_dir = Path("temp_audio")
    temp_dir.mkdir(exist_ok=True)

    db = init_firebase()

    print("Fetching animals from Firestore...")
    animals_ref = db.collection('animals')
    animals = animals_ref.stream()

    success_count = 0
    fail_count = 0

    for animal_doc in animals:
        animal_data = animal_doc.to_dict()
        animal_data['id'] = animal_doc.id

        try:
            if process_animal(animal_data, temp_dir):
                success_count += 1
            else:
                fail_count += 1
        except Exception as e:
            print(f"✗ Error processing {animal_data.get('name', 'Unknown')}: {e}")
            fail_count += 1

    print(f"\n{'='*60}")
    print(f"Processing Complete!")
    print(f"Success: {success_count}")
    print(f"Failed: {fail_count}")
    print(f"{'='*60}")

    if success_count > 0:
        print(f"\nCleaning up temporary files...")
        try:
            shutil.rmtree(temp_dir)
            print(f"✓ Temporary files cleaned up")
        except Exception as e:
            print(f"✗ Failed to clean up temp files: {e}")
            print(f"  You can manually delete the '{temp_dir}' folder")

if __name__ == "__main__":
    main()
