import os
from dotenv import load_dotenv
from langchain_google_vertexai import VertexAI, VertexAISearchRetriever
from langchain.chains import ConversationalRetrievalChain
from langchain.prompts import PromptTemplate

# Load environment variables
load_dotenv()

# Configuration from .env
PROJECT_ID = os.getenv("GOOGLE_CLOUD_PROJECT_ID")
DATA_STORE_ID = os.getenv("VERTEX_AI_DATA_STORE_ID")
LOCATION = os.getenv("VERTEX_AI_LOCATION", "global")
MODEL_NAME = os.getenv("GEMINI_MODEL_NAME", "gemini-1.5-flash")

def main():
    print("Initializing Vertex AI Grounded Search Agent...")
    
    if not PROJECT_ID or not DATA_STORE_ID:
        print("ERROR: Missing required environment variables.")
        print("Please set GOOGLE_CLOUD_PROJECT_ID and VERTEX_AI_DATA_STORE_ID in your .env file.")
        return

    try:
        # 1. Initialize the Retriever (This consumes the $1000 credits)
        print(f"Connecting to Data Store: {DATA_STORE_ID} in {LOCATION}...")
        retriever = VertexAISearchRetriever(
            project_id=PROJECT_ID,
            location_id=LOCATION,
            data_store_id=DATA_STORE_ID,
            get_extractive_answers=True, # Recommended for credits
        )

        # 2. Set up the LLM
        print(f"Loading Model: {MODEL_NAME}...")
        llm = VertexAI(model_name=MODEL_NAME)

        # 3. Create the System Prompt enforcing Grounding
        prompt_template = """
        Role: You are a Grounded Knowledge Assistant. 
        Objective: Answer user queries strictly using the provided search results from the Vertex AI Search data store.

        Instructions:
        1. Grounding: Do not use outside knowledge. If the search results do not contain the answer, state: "I'm sorry, I don't have information on that topic in my database."
        2. Citations: Every claim you make must be followed by a citation.
        3. Response Format: Provide a concise summary first, followed by detailed bullet points if necessary.
        4. Tone: Maintain a professional and helpful tone.

        Context: 
        {context}

        User Query: {question}
        """
        
        prompt = PromptTemplate(
            template=prompt_template, input_variables=["context", "question"]
        )

        # 4. Create the Grounded Chain
        qa_chain = ConversationalRetrievalChain.from_llm(
            llm=llm,
            retriever=retriever,
            combine_docs_chain_kwargs={"prompt": prompt},
            return_source_documents=True
        )

        print("\nAgent is ready! (Type 'quit' to exit)")
        print("-" * 50)
        
        chat_history = []
        
        # Interactive Loop
        while True:
            query = input("\nYou: ")
            if query.lower() in ['quit', 'exit', 'q']:
                break
                
            if not query.strip():
                continue
                
            print("\nSearching Vertex AI Data Store...")
            
            try:
                response = qa_chain.invoke({
                    "question": query, 
                    "chat_history": chat_history
                })
                
                print("\nAgent:", response["answer"])
                
                # Optional: Update chat history for multi-turn conversations
                chat_history.append((query, response["answer"]))
                
            except Exception as e:
                print(f"\nError during retrieval/generation: {e}")

    except Exception as e:
        print(f"\nFailed to initialize: {e}")
        print("Ensure you are authenticated with Google Cloud (run: gcloud auth application-default login)")

if __name__ == "__main__":
    main()