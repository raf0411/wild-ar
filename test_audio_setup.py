"""
Simple Audio Test Script
Test 11Labs API and Supabase Storage upload with a single animal before running the full script
"""

import os
import requests
from supabase import create_client
from pathlib import Path

# Test Configuration - FILL THESE IN
ELEVENLABS_API_KEY = "your_elevenlabs_api_key"
SUPABASE_URL = "your_supabase_url"  # e.g., https://xxxxx.supabase.co
SUPABASE_KEY = "your_supabase_key"  # anon/public key
SUPABASE_BUCKET = "faunadex-audio"

# Test data
TEST_TEXT = "Komodo adalah kadal terbesar di dunia. Mereka bisa tumbuh hingga 3 meter panjangnya dan memiliki gigitan beracun yang kuat."
TEST_ANIMAL_ID = "test_komodo"
VOICE_ID = "21m00Tcm4TlvDq8ikWAM"  # Rachel voice

def test_elevenlabs():
    """Test 11Labs API"""
    print("\n" + "="*60)
    print("Testing 11Labs API...")
    print("="*60)

    url = f"https://api.elevenlabs.io/v1/text-to-speech/{VOICE_ID}"

    headers = {
        "Accept": "audio/mpeg",
        "Content-Type": "application/json",
        "xi-api-key": ELEVENLABS_API_KEY
    }

    data = {
        "text": TEST_TEXT,
        "model_id": "eleven_multilingual_v2",
        "voice_settings": {
            "stability": 0.5,
            "similarity_boost": 0.75
        }
    }

    print(f"Generating audio for: {TEST_TEXT[:50]}...")
    response = requests.post(url, json=data, headers=headers)

    if response.status_code == 200:
        output_path = "test_audio.mp3"
        with open(output_path, 'wb') as f:
            f.write(response.content)
        print(f"✅ SUCCESS! Audio saved to: {output_path}")
        print(f"   File size: {len(response.content) / 1024:.2f} KB")
        print(f"   You can play it to test the voice quality!")
        return output_path
    else:
        print(f"❌ FAILED!")
        print(f"   Status: {response.status_code}")
        print(f"   Error: {response.text}")
        return None

def test_supabase_upload(local_file):
    """Test Supabase Storage upload"""
    print("\n" + "="*60)
    print("Testing Supabase Storage Upload...")
    print("="*60)

    try:
        supabase = create_client(SUPABASE_URL, SUPABASE_KEY)

        storage_path = f"audio/test/{TEST_ANIMAL_ID}.mp3"
        print(f"Uploading to Supabase bucket: {SUPABASE_BUCKET}/{storage_path}")

        with open(local_file, 'rb') as f:
            file_content = f.read()

        # Upload file
        response = supabase.storage.from_(SUPABASE_BUCKET).upload(
            storage_path,
            file_content,
            file_options={"content-type": "audio/mpeg"}
        )

        # Get public URL
        public_url = supabase.storage.from_(SUPABASE_BUCKET).get_public_url(storage_path)

        print(f"✅ SUCCESS! Audio uploaded to Supabase")
        print(f"   Public URL: {public_url}")
        print(f"   You can open this URL in a browser to test!")
        return public_url

    except Exception as e:
        print(f"❌ FAILED!")
        print(f"   Error: {str(e)}")
        return None

def main():
    print("\n🎵 FaunaDex Audio System Test 🎵\n")

    # Validate configuration
    if "your_" in ELEVENLABS_API_KEY:
        print("❌ Please update the configuration at the top of this script!")
        print("   Fill in your actual API keys and credentials.")
        return

    # Test 11Labs
    audio_file = test_elevenlabs()
    if not audio_file:
        print("\n⚠️  11Labs test failed. Fix the issue before proceeding.")
        return

    # Test Supabase
    public_url = test_supabase_upload(audio_file)
    if not public_url:
        print("\n⚠️  Supabase upload test failed. Fix the issue before proceeding.")
        return

    # Summary
    print("\n" + "="*60)
    print("🎉 ALL TESTS PASSED!")
    print("="*60)
    print("✅ 11Labs API is working")
    print("✅ Supabase upload is working")
    print("✅ Public URL is accessible")
    print("\nNext Steps:")
    print("1. Open the test_audio.mp3 file and listen to it")
    print("2. Open the public URL in a browser to verify it plays")
    print("3. If everything sounds good, run audio_generator.py")
    print("="*60)

if __name__ == "__main__":
    main()
