#!/bin/bash

# Create a directory for JavaFX if it doesn't exist
mkdir -p ~/.javafx

# Download JavaFX SDK if not already present
if [ ! -d ~/.javafx/javafx-sdk-21.0.6 ]; then
    echo "Downloading JavaFX SDK..."
    curl -L -o javafx.zip https://download2.gluonhq.com/openjfx/21.0.6/openjfx-21.0.6_osx-x64_bin-sdk.zip
    unzip javafx.zip -d ~/.javafx
    rm javafx.zip
    mv ~/.javafx/javafx-sdk-21.0.6 ~/.javafx/
fi

# Add to .zshrc if not already present
if ! grep -q "JAVAFX_HOME" ~/.zshrc; then
    echo "Adding JAVAFX_HOME to .zshrc..."
    echo 'export JAVAFX_HOME=~/.javafx/javafx-sdk-21.0.6' >> ~/.zshrc
    echo "Please restart your terminal or run: source ~/.zshrc"
fi

echo "JavaFX setup complete!"
echo "JAVAFX_HOME is set to: ~/.javafx/javafx-sdk-21.0.6" 