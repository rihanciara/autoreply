# OpenCode Vertex Agent (VS Code Extension)

**Goal:** Provide an "Agentic" (OpenCode/Antigravity style) VS Code Extension that strictly routes your coding queries through Google Cloud's **GenAI App Builder (Vertex AI Search)** to consume your $1,000 promotional credits.

## Features
- 🚀 **VS Code Native Sidebar:** Appears right inside your code editor like GitHub Copilot or OpenCode.
- 🧠 **Context-Aware Agent:** Automatically reads the file you are currently working on in VS Code and uses it to augment your Vertex AI Search queries.
- 💳 **Credit Burner:** Explicitly uses the `discoveryengine.googleapis.com` API endpoint which qualifies for the $1000 promotional credits.

## Installation Instructions

### 1. Requirements
Ensure you have Node.js and `npm` installed on your machine.

### 2. Install Dependencies
Open your terminal in the `vscode-vertex-agent` directory and run:
```bash
npm install
```

### 3. Compile the Extension
```bash
npm run compile
```

### 4. Run / Debug in VS Code
1. Open the `vscode-vertex-agent` folder in VS Code.
2. Press `F5` to open a new VS Code window with the extension loaded.

### 5. Setup your GenAI App Builder Settings
1. In the new VS Code window, open settings (`Ctrl+,` or `Cmd+,`).
2. Search for `opencodeVertex`.
3. Enter your **GCP Project ID** and your **Vertex AI Data Store ID**.

### 6. Authenticate Google Cloud
To bill your free credits, open a terminal on your computer and authenticate:
```bash
gcloud auth application-default login
```

## How to Use
1. Click the new **OpenCode** icon in your left sidebar.
2. Open a code file you are working on.
3. Type a question in the chat box (e.g., "How do I test this function?").
4. The agent will read your file, send the context to your GenAI App Builder Data Store, and return a grounded summary using your free credits!
