#!/usr/bin/env python3
# docker-run.py
import os
import sys
from itertools import batched
from shutil import copytree, rmtree
import glob

INPUT_DIR = "/input-project"
TARGET_DIR = "/target-project"

def astor_jar() -> str:
    matches = glob.glob("/astor/target/astor-*-jar-with-dependencies.jar")
    if not matches:
        print("Astor jar not found in /astor/target")
        exit(1)
    return matches[0]

def prepare_target_project() -> None:
    if not os.path.isdir(INPUT_DIR):
        print(f"No mounted project in {INPUT_DIR}. Use -v <project>:{INPUT_DIR}:ro")
        exit(1)
    if os.path.exists(TARGET_DIR):
        rmtree(TARGET_DIR)
    copytree(INPUT_DIR, TARGET_DIR)

# Allowed arguments. See docs/arguments.md
ALLOWED_ARGS: set[str] = {
    "javacompliancelevel",
    "location",
    "srcjavafolder",
    "srctestfolder",
    "binjavafolder",
    "bintestfolder",
    "resourcesfolder",
    "mode",
    "maxtime",
    "maxGeneration",
    "population",
    "workingDirectory",
    "stopfirst",
    "maxnumbersolutions",
    "maxsuspcandidates",
    "faultlocalization",
    "maxmodificationpoints",
    "flthreshold",
    "regressionforfaultlocalization",
    "packageToInstrument",
    "skipfaultlocalization",
    "skipfitnessinitialpopulation",
    "scope",
    "modificationpointnavigation",
    "mutationrate",
    "validation",
    "tmax1",
    "tmax2",
    "overridemaxtime",
    "testbystep",
    "ignoredTestCases",
    "savesolution",
    "saveall",
    "multipointmodification",
    "pvariantfoldername",
    "loglevel",
    "logpatternlayout",
    "jsonoutputname",
    "parameters"
}

def build_command(argv: list[str]) -> list[str]:
    args: list[str] = [
        "java",
        "-cp",
        astor_jar(),
        "fr.inria.main.evolution.AstorMain",
        "-location",
        "/target-project"
    ]
    # We iterate the parameters as a batch, so we can catch the parameter's value
    for pair in batched(argv, 2):
        if len(pair) != 2:
            print(f"Missing value for parameter: {pair[0]}")
            exit(1)
            
        if pair[0][1:] == "location":
            # We override the location to point to our /target-project
            continue
            
        if pair[0][1:] not in ALLOWED_ARGS:
            print(f"An unknown parameter has been given: {pair[0]}")
            exit(1)
        args.append(pair[0])
        args.append(pair[1])

    return args


if __name__ == "__main__":
    argv = sys.argv[1:]
    if len(argv) == 0:
        print(f"Usage: {sys.argv[0]} <parameters>")
        exit(1)

    prepare_target_project()
    command = build_command(argv)
    os.execvp(command[0], command)