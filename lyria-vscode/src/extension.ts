import * as vscode from 'vscode';
import * as cp from 'child_process';
import * as util from 'util';

const exec = util.promisify(cp.exec);

export function activate(context: vscode.ExtensionContext) {
    console.log('Lyria Agent is now active!');

    // Create an output channel to show Lyria's responses
    const outputChannel = vscode.window.createOutputChannel('Lyria Agent');

    // Command to ask Lyria a question via input box
    let askCommand = vscode.commands.registerCommand('lyria.ask', async () => {
        const query = await vscode.window.showInputBox({
            prompt: "Ask Lyria a question about your code (Uses Google Cloud GenAI App Builder credits)",
            placeHolder: "E.g., How does the authentication flow work in this project?"
        });

        if (query) {
            outputChannel.show(true);
            outputChannel.appendLine(`\n[You]: ${query}`);
            outputChannel.appendLine(`[Lyria]: Thinking... (Querying Vertex AI Data Store)`);

            try {
                // Execute the vertex-ask python CLI tool
                const { stdout, stderr } = await exec(`vertex-ask "${query}"`);
                
                if (stderr) {
                    console.error('Lyria Error:', stderr);
                }
                
                outputChannel.appendLine(`[Lyria]:\n${stdout}`);
            } catch (error: any) {
                vscode.window.showErrorMessage(`Lyria failed to respond: ${error.message}`);
                outputChannel.appendLine(`[Error]: ${error.message}`);
            }
        }
    });

    // Command to explain selected code
    let explainCommand = vscode.commands.registerCommand('lyria.explainSelection', async () => {
        const editor = vscode.window.activeTextEditor;
        if (!editor) {
            vscode.window.showInformationMessage('No active editor found.');
            return;
        }

        const selection = editor.selection;
        const text = editor.document.getText(selection);

        if (!text) {
            vscode.window.showInformationMessage('Please select some code to explain.');
            return;
        }

        outputChannel.show(true);
        outputChannel.appendLine(`\n[You]: Please explain the selected code:\n\`\`\`\n${text}\n\`\`\``);
        outputChannel.appendLine(`[Lyria]: Thinking... (Querying Vertex AI Data Store)`);

        try {
            // Escape double quotes for bash
            const safeText = text.replace(/"/g, '\\"');
            const query = `Please explain this code snippet:\n${safeText}`;
            
            const { stdout } = await exec(`vertex-ask "${query}"`);
            outputChannel.appendLine(`[Lyria]:\n${stdout}`);
        } catch (error: any) {
            vscode.window.showErrorMessage(`Lyria failed to respond: ${error.message}`);
            outputChannel.appendLine(`[Error]: ${error.message}`);
        }
    });

    context.subscriptions.push(askCommand, explainCommand, outputChannel);
}

export function deactivate() {}
