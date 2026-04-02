# OpenCode Vertex AI Extension

**The Problem:** You have $1,000 in free Google Cloud "GenAI App Builder" (Vertex AI Search and Conversation) credits that you want to use for your coding tasks, but most standard coding assistants use their own billing accounts or direct LLM APIs that do not consume these specific credits.

**The Solution:** This is a custom OpenCode Extension specifically designed to act as your daily Grounded Knowledge Assistant. 
By utilizing `VertexAISearchRetriever` underneath, every query you make through this terminal extension forces Google to query your Vertex AI Data Store. 
**This guarantees that you are consuming your $1,000 GenAI App Builder credits instead of paying out of pocket for tokens.**

## 🚀 Features

*   **100% Credit Eligible:** Hardcoded to trigger the Vertex AI Discovery Engine API.
*   **Grounded Generation:** It strictly answers your coding queries based on the documents you indexed in your Data Store.
*   **Interactive Terminal:** Functions just like an OpenCode CLI agent. 

---

## 🛠️ Complete Installation Documentation

### Step 1: Clone and Install
First, ensure you have Python installed. Then, clone this repository and install the custom extension as a global CLI tool.

```bash
# Clone the repo (if you haven't already)
git clone https://github.com/rihanciara/autoreply.git
cd autoreply

# Install the OpenCode extension locally
pip install -e .
```

### Step 2: Authenticate Google Cloud
To bill the operations to your GCP account (and your free credits), you must authenticate the local terminal. Ensure you have the `gcloud` CLI installed from Google.

```bash
gcloud auth application-default login
```

### Step 3: Configure Your Environment
The extension requires your specific Data Store IDs to work. 

1. Copy the example configuration file:
   ```bash
   cp .env.example .env
   ```
2. Open `.env` and fill in your details from the Google Cloud Console:
   * `GOOGLE_CLOUD_PROJECT_ID`: Find this in your GCP dashboard.
   * `VERTEX_AI_DATA_STORE_ID`: Find this in the "Vertex AI Search" tab.

---

## 💻 How to Use the OpenCode Extension

Once installed, a new global command `vertex-ask` becomes available in your terminal. You can use it anywhere on your computer while coding!

### 1. Interactive Chat Mode
Start a continuous chat session to ask questions about the documents or codebases you uploaded to Vertex AI Search:

```bash
vertex-ask -i
```
*(Type your questions, and the agent will fetch the context using your credits).*

### 2. Single Query Mode
Quickly ask a question without opening the interactive terminal:

```bash
vertex-ask "What is the authentication logic for the production server?"
```

## 📝 Proof of Billing
To verify that your $1000 credits are being used:
1. Make 5-10 queries using the `vertex-ask` tool.
2. Go to the Google Cloud Console -> **Billing**.
3. Look at the Reports tab. You will see usage under **"Vertex AI Search"** or **"Discovery Engine API"**, which is completely covered by your GenAI App Builder promotional credits!