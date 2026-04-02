# Android Auto-Reply Call Screener (Proof of Concept)

**⚠️ CRITICAL NOTICE: THIS PROJECT CANNOT FUNCTION AS INTENDED ON MODERN NON-ROOTED ANDROID DEVICES ⚠️**

This repository contains the source code for an Android application designed to act as an automated IVR (Interactive Voice Response) or Call Screener for incoming cellular calls. 

The intended goal of this app was to automatically (or manually) answer calls from unknown numbers and immediately play a pre-recorded audio file (e.g., an MP3 message) so the remote caller could hear it, followed by waiting for the caller's response (staying on the line).

## Why This Project Does Not Work

Due to severe security and hardware restrictions built deeply into the Android Operating System (specifically Android 10+), **it is fundamentally impossible for a standard, third-party app to reliably inject audio into a live cellular phone call.**

Here are the specific technical roadblocks that prevent this app (and any similar app on the Google Play Store) from working:

### 1. No Direct Audio Injection (Blocked by OS)
Android strictly prohibits third-party applications from injecting digital audio (like an MP3 file) directly into the cellular uplink (Tx) stream (`STREAM_VOICE_CALL`). This is a security measure designed to prevent malicious apps from spoofing your voice or intercepting phone calls. Only highly privileged System Apps (like the phone's default OEM Dialer or Google's Pixel "Call Screen") have the necessary baseband modem access to route digital audio into a call.

### 2. The "Speakerphone Hack" Fails (Blocked by Hardware AEC)
To bypass the digital restriction, this app attempts a common physical workaround:
* When a call is answered, it forces the phone's physical speakerphone ON (`AudioManager.isSpeakerphoneOn = true`).
* It plays the MP3 out loud into the room, hoping the phone's physical microphone picks it up and transmits it to the remote caller.

**This fails completely on modern phones.** Modern smartphones use hardware-level Acoustic Echo Cancellation (AEC). When the device detects that its own speaker is playing media during a call, the AEC chip aggressively mutes or filters the microphone input to prevent the remote caller from hearing a terrible echo of their own voice. As a result, the MP3 plays loudly in your room, but the remote caller hears **dead silence**.

### 3. Keypad (DTMF) Detection is Impossible (Blocked by TelephonyManager)
The original goal was to ask the caller to "Press 1" to continue. However, Android's `TelephonyManager` physically blocks third-party apps from detecting DTMF keypad tones over a live cellular network. This is a vital security rule to prevent apps from stealing credit card PINs or bank passwords typed during a call. Because the OS blocks the "beep" sound from reaching the app, detecting a button press is impossible.

## The Only Working Alternatives

If you need a true IVR system where callers hear crystal-clear audio and can press buttons to interact, you **cannot** build it directly on a standard Android phone's SIM card.

You must use a Cloud Telephony solution:
1. **Cloud Phone Numbers (e.g., Twilio, Plivo):** You rent a virtual phone number.
2. **Cloud Servers:** When someone calls that virtual number, the cloud server answers it instantly, plays the audio perfectly over the phone line, and listens for keypad presses.
3. **Call Forwarding:** If the caller presses the correct button, the cloud server forwards the call to your actual personal cell phone so you can talk to them.

This approach requires no Android app, bypasses all hardware restrictions, and provides a professional experience for the caller.