# Vertex AI Grounded Search Agent

This Python project connects a LangChain conversational agent to Google Cloud Vertex AI Search. It is designed to act as an "open code" Grounded Knowledge Assistant, ensuring that it uses the `VertexAISearchRetriever` tool so your queries are billed against the $1,000 GenAI App Builder (Vertex AI Search and Conversation) credits.

## Prerequisites

1.  **Google Cloud Platform Account** with Vertex AI enabled.
2.  **Vertex AI Search Data Store:** You must have already created an active data store and indexed documents in your GCP console.
3.  **Google Cloud CLI:** Installed and authenticated on your machine.

## Setup Instructions

1.  **Install Dependencies:**
    Install the required Python libraries using pip:
    ```bash
    pip install -r requirements.txt
    ```

2.  **Authenticate with Google Cloud:**
    You must authenticate your local environment so the script can access your GCP project:
    ```bash
    gcloud auth application-default login
    ```

3.  **Configure Environment Variables:**
    Copy the example `.env` file to a real one:
    ```bash
    cp .env.example .env
    ```
    Open the `.env` file and replace the placeholder values with your actual project details:
    *   `GOOGLE_CLOUD_PROJECT_ID`: Your GCP Project ID.
    *   `VERTEX_AI_DATA_STORE_ID`: The ID of the specific Data Store you want to query.
    *   `VERTEX_AI_LOCATION`: Usually `global` unless you specified a region.

## Running the Agent

Run the script to start the interactive chat terminal:

```bash
python main.py
```

Type your questions, and the agent will use Vertex AI Search to fetch the relevant context (using your GenAI App Builder credits) and generate a grounded answer!
