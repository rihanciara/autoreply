import click
import sys
import os

# Add parent directory to path for imports
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from opencode_vertex_extension.core import VertexSearchAgent

@click.command()
@click.option('--interactive', '-i', is_flag=True, help="Start an interactive OpenCode Vertex session.")
@click.argument('query', required=False)
def main(interactive, query):
    """
    OpenCode Extension: A Grounded Agent for Vertex AI GenAI App Builder.
    This routes your queries through Vertex AI Search to specifically burn your $1000 credits.
    """
    print("\n" + "="*50)
    print("🚀 OpenCode Extension: Vertex AI Grounded Search")
    print("="*50 + "\n")

    try:
        agent = VertexSearchAgent()
    except Exception as e:
        click.echo(click.style(f"Initialization Failed: {e}", fg="red"))
        sys.exit(1)

    if interactive or not query:
        click.echo(click.style("\nInteractive mode started. Type 'quit' to exit.", fg="green"))
        while True:
            try:
                user_input = click.prompt(click.style("\nOpenCode User", fg="blue"))
                if user_input.lower() in ['quit', 'exit', 'q']:
                    break
                if not user_input.strip():
                    continue
                
                click.echo(click.style("Agent querying Vertex AI Data Store...", dim=True))
                answer = agent.ask(user_input)
                click.echo(click.style(f"\nOpenCode Agent:\n{answer}", fg="cyan"))
            except (KeyboardInterrupt, EOFError):
                break
    else:
        click.echo(click.style("Agent querying Vertex AI Data Store...", dim=True))
        answer = agent.ask(query)
        click.echo(click.style(f"\nOpenCode Agent:\n{answer}", fg="cyan"))

if __name__ == "__main__":
    main()