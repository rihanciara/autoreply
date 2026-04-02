import os
from dotenv import load_dotenv
from langchain_google_vertexai import VertexAI, VertexAISearchRetriever
from langchain.chains import ConversationalRetrievalChain
from langchain.prompts import PromptTemplate

class VertexSearchAgent:
    def __init__(self):
        load_dotenv()
        self.project_id = os.getenv("GOOGLE_CLOUD_PROJECT_ID")
        self.data_store_id = os.getenv("VERTEX_AI_DATA_STORE_ID")
        self.location = os.getenv("VERTEX_AI_LOCATION", "global")
        self.model_name = os.getenv("GEMINI_MODEL_NAME", "gemini-1.5-flash")
        self.chat_history = []
        
        self._validate_env()
        self.chain = self._initialize_chain()

    def _validate_env(self):
        if not self.project_id or not self.data_store_id:
            raise ValueError(
                "MISSING ENVIRONMENT VARIABLES:\n"
                "Please configure GOOGLE_CLOUD_PROJECT_ID and VERTEX_AI_DATA_STORE_ID in your .env file.\n"
                "These are required to consume your GenAI App Builder credits."
            )

    def _initialize_chain(self):
        print(f"[*] Initializing Vertex AI Grounded Search (Data Store: {self.data_store_id})")
        print("[*] Note: This retrieval process consumes your $1,000 GenAI App Builder credits.")

        # 1. Initialize the Retriever (This specific component bills against the credits)
        retriever = VertexAISearchRetriever(
            project_id=self.project_id,
            location_id=self.location,
            data_store_id=self.data_store_id,
            get_extractive_answers=True,
        )

        # 2. Set up the LLM for grounded generation
        llm = VertexAI(model_name=self.model_name)

        # 3. Create the System Prompt forcing grounding
        prompt_template = """
        Role: You are an OpenCode Grounded Knowledge Assistant. 
        Objective: Answer user queries strictly using the provided search results from the Vertex AI Search data store.

        Instructions:
        1. Retrieval Protocol: For every query, you must first evaluate the retrieved context.
        2. Grounding: Do not use outside knowledge to answer factual questions. If the search results do not contain the answer, state: "I'm sorry, I don't have information on that topic in my database."
        3. Citations: Every claim you make must be followed by a citation based on the metadata provided in the search results.
        4. Tone: Maintain a professional and helpful tone for software engineers.

        Context: 
        {context}

        User Query: {question}
        """
        
        prompt = PromptTemplate(
            template=prompt_template, input_variables=["context", "question"]
        )

        # 4. Create the Grounded Chain
        return ConversationalRetrievalChain.from_llm(
            llm=llm,
            retriever=retriever,
            combine_docs_chain_kwargs={"prompt": prompt},
            return_source_documents=True
        )

    def ask(self, query: str):
        try:
            response = self.chain.invoke({
                "question": query, 
                "chat_history": self.chat_history
            })
            
            # Append to memory
            self.chat_history.append((query, response["answer"]))
            
            return response["answer"]
        except Exception as e:
            return f"Error connecting to Vertex AI: {e}\n(Ensure you ran 'gcloud auth application-default login')"