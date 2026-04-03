# OpenCode Core Extension Overhaul

## The Goal
The user wants to bring the full power of "OpenCode" (an agentic coding tool based on Claude Dev / Cline) to their VS Code environment, specifically modifying it to use their Google Cloud "GenAI App Builder" free credits (Vertex AI Search/Discovery Engine) instead of traditional API endpoints that charge per token.

## What Was Done
1. Created a new branch `lyria-vscode-extension` in the `autoreply` repository.
2. Built a scaffolding for a VS Code extension named **Lyria Agent** inside `lyria-vscode/`.
3. Created an `extension.ts` that provides basic VS Code commands (`lyria.ask` and `lyria.explainSelection`) which act as a wrapper around the `vertex-ask` CLI tool built in this repository.
4. Cloned the core `opencode_core` (based on `saoudrizwan/claude-dev`) into the root directory to examine how its native API handlers (`src/core/api/providers/vertex.ts` and `gemini.ts`) operate.

## Next Steps for the OpenCode Fork
To fully grant OpenCode the power to use GenAI App Builder credits, the following needs to be modified in the `opencode_core` repository:

1. **API Provider Override:** We need to modify `src/core/api/providers/vertex.ts` or create a new provider (e.g., `discovery_engine.ts`). 
2. **Langchain Integration:** Instead of using the standard `@anthropic-ai/vertex-sdk` or `@google/genai` SDKs, the API handler needs to be rewritten to utilize the `VertexAISearchRetriever` architecture shown in `autoreply/opencode_vertex_extension/core.py`.
3. **Chain Execution:** When the OpenCode agent issues a `createMessage` or `streamMessage` request, it must pass the conversation history into the `ConversationalRetrievalChain` mapped to the user's `VERTEX_AI_DATA_STORE_ID`.
