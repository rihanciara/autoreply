import os
from setuptools import setup, find_packages

setup(
    name="opencode-vertex-extension",
    version="1.0.0",
    description="An OpenCode extension to route AI queries through GCP GenAI App Builder (Vertex AI Search) to consume free credits.",
    author="Your Name",
    packages=find_packages(),
    install_requires=[
        "google-cloud-aiplatform",
        "langchain-google-vertexai",
        "langchain",
        "python-dotenv",
        "click"
    ],
    entry_points={
        "console_scripts": [
            "vertex-ask=opencode_vertex_extension.cli:main",
        ],
    },
)