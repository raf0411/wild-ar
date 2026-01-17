"""
Audio Generator Script for FaunaDex
Generates audio narrations using 11Labs API and uploads to Supabase Storage

Requirements:
    pip install requests firebase-admin supabase

Environment Variables:
    ELEVENLABS_API_KEY: Your 11Labs API key
    SUPABASE_URL: Your Supabase project URL (e.g., https://xxxxx.supabase.co)
    SUPABASE_KEY: Your Supabase service role key (anon key)
    SUPABASE_BUCKET: Your Supabase storage bucket name (default: faunadex-audio)
"""

import os
import json
import requests
from pathlib import Path
import firebase_admin
from firebase_admin import credentials, firestore
from supabase import create_client, Client

# Configuration
ELEVENLABS_API_KEY = os.getenv("ELEVENLABS_API_KEY")
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY")
SUPABASE_BUCKET = os.getenv("SUPABASE_BUCKET", "faunadex-audio")

# 11Labs Voice IDs (you can change these)
VOICE_ID_SD = "EXAVITQu4vr4xnSDxMaL"  # Sarah - friendly, clear for kids
VOICE_ID_SMP = "21m00Tcm4TlvDq8ikWAM"  # Rachel - mature, engaging
VOICE_ID_SMA = "21m00Tcm4TlvDq8ikWAM"  # Rachel - same for SMA

# Initialize Firebase (adjust path to your service account key)
SERVICE_ACCOUNT_KEY = "path/to/your/serviceAccountKey.json"

def init_firebase():
    """Initialize Firebase Admin SDK"""
    if not firebase_admin._apps:
        cred = credentials.Certificate(SERVICE_ACCOUNT_KEY)
        firebase_admin.initialize_app(cred)
    return firestore.client()

def init_supabase_client():
    """Initialize Supabase client"""
    return create_client(SUPABASE_URL, SUPABASE_KEY)

def generate_audio_elevenlabs(text, voice_id, output_path):
    """Generate audio using 11Labs API"""
    url = f"https://api.elevenlabs.io/v1/text-to-speech/{voice_id}"

    headers = {
        "Accept": "audio/mpeg",
        "Content-Type": "application/json",
        "xi-api-key": ELEVENLABS_API_KEY
    }

    data = {
        "text": text,
        "model_id": "eleven_multilingual_v2",  # Supports Indonesian
        "voice_settings": {
            "stability": 0.5,
            "similarity_boost": 0.75,
            "style": 0.0,
            "use_speaker_boost": True
        }
    }

    response = requests.post(url, json=data, headers=headers)

    if response.status_code == 200:
        with open(output_path, 'wb') as f:
            f.write(response.content)
        print(f"✓ Audio generated: {output_path}")
        return True
    else:
        print(f"✗ Failed to generate audio: {response.status_code} - {response.text}")
        return False

def upload_to_supabase(local_path, storage_path):
    """Upload file to Supabase Storage"""
    supabase = init_supabase_client()

    try:
        with open(local_path, 'rb') as f:
            file_content = f.read()

        # Upload to Supabase Storage
        response = supabase.storage.from_(SUPABASE_BUCKET).upload(
            storage_path,
            file_content,
            file_options={"content-type": "audio/mpeg"}
        )

        # Get public URL
        public_url = supabase.storage.from_(SUPABASE_BUCKET).get_public_url(storage_path)
        print(f"✓ Uploaded to Supabase: {public_url}")
        return public_url
    except Exception as e:
        print(f"✗ Failed to upload to Supabase: {e}")
        return None

def update_firestore_audio_url(animal_id, audio_description_url, audio_fun_fact_url=None):
    """Update animal document in Firestore with audio URLs"""
    db = init_firebase()
    animal_ref = db.collection('animals').document(animal_id)

    update_data = {
        'audio_description_url': audio_description_url
    }

    if audio_fun_fact_url:
        update_data['audio_fun_fact_url'] = audio_fun_fact_url

    animal_ref.update(update_data)
    print(f"✓ Updated Firestore for animal: {animal_id}")

def process_animal(animal_data, temp_dir):
    """Process a single animal: generate audio and upload"""
    animal_id = animal_data['id']
    animal_name = animal_data.get('name', 'Unknown')
    description = animal_data.get('description', '')
    fun_fact = animal_data.get('fun_fact', '')

    print(f"\n{'='*60}")
    print(f"Processing: {animal_name} (ID: {animal_id})")
    print(f"{'='*60}")

    # Skip if no description
    if not description:
        print("✗ No description found, skipping...")
        return False

    # Choose voice based on education level (you can customize this)
    voice_id = VOICE_ID_SMP  # Default voice

    # Generate description audio
    desc_filename = f"{animal_id}_description.mp3"
    desc_local_path = temp_dir / desc_filename

    print(f"Generating description audio...")
    if generate_audio_elevenlabs(description, voice_id, desc_local_path):
        # Upload to Supabase
        desc_storage_path = f"audio/descriptions/{desc_filename}"
        desc_url = upload_to_supabase(desc_local_path, desc_storage_path)

        # Generate fun fact audio (if available)
        funfact_url = None
        if fun_fact:
            print(f"Generating fun fact audio...")
            funfact_filename = f"{animal_id}_funfact.mp3"
            funfact_local_path = temp_dir / funfact_filename

            if generate_audio_elevenlabs(fun_fact, voice_id, funfact_local_path):
                funfact_storage_path = f"audio/funfacts/{funfact_filename}"
                funfact_url = upload_to_supabase(funfact_local_path, funfact_storage_path)

        # Update Firestore
        if desc_url:
            update_firestore_audio_url(animal_id, desc_url, funfact_url)
            return True

    return False

def main():
    """Main function to process all animals"""
    # Validate environment variables
    required_vars = [
        "ELEVENLABS_API_KEY",
        "SUPABASE_URL",
        "SUPABASE_KEY",
        "SUPABASE_BUCKET"
    ]

    missing_vars = [var for var in required_vars if not os.getenv(var)]
    if missing_vars:
        print(f"✗ Missing environment variables: {', '.join(missing_vars)}")
        return

    # Create temp directory for audio files
    temp_dir = Path("temp_audio")
    temp_dir.mkdir(exist_ok=True)

    # Initialize Firebase
    db = init_firebase()

    # Fetch all animals from Firestore
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

if __name__ == "__main__":
    main()
