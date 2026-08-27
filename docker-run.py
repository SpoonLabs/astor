#!/usr/bin/env python3
# docker-run.py
import os
import sys
from itertools import batched
from shutil import copytree, rmtree
import glob

INPUT_DIR = "/input-project"


def astor_jar() -> str:
    matches = glob.glob("/astor/target/astor-*-jar-with-dependencies.jar")
    if not matches:
        print("Astor jar not found in /astor/target")
        exit(1)
    return matches[0]


def prepare_target_project() -> str:
    if not os.path.isdir(INPUT_DIR):
        print(f"No mounted project in {INPUT_DIR}. Use -v <project>:{INPUT_DIR}/<name>:ro")
        exit(1)

    entries = os.listdir(INPUT_DIR)
    if len(entries) != 1:
        print(f"Expected exactly one project mounted in {INPUT_DIR}, found: {entries}")
        exit(1)

    project_name = entries[0]
    source = os.path.join(INPUT_DIR, project_name)
    target = os.path.join("/astor", project_name)

    if os.path.exists(target):
        rmtree(target)
    copytree(source, target)

    return target


# Allowed arguments. See docs/arguments.md
ALLOWED_ARGS: set[str] = {
    "javacompliancelevel",
    "srcjavafolder",
    "srctestfolder",
    "binjavafolder",
    "bintestfolder",
    "resourcesfolder",
    "mode",
    "maxtime",
    "maxGeneration",
    "population",
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
    "parameters",
}

# Parameters that shouldn't be allowed as the container should deal with the hard work
BLACKLISTED_ARGS: set[str] = {
    "location",
    "workingDirectory",
}

def build_command(argv: list[str], target_dir: str) -> list[str]:
    args: list[str] = [
        "java",
        "-cp",
        astor_jar(),
        "fr.inria.main.evolution.AstorMain",
        "-location",
        target_dir,
    ]
    # We iterate the parameters as a batch, so we can catch the parameter's value
    for pair in batched(argv, 2):
        if len(pair) != 2:
            print(f"Missing value for parameter: {pair[0]}")
            exit(1)
        
        param: str = pair[0][1:]
        if param in BLACKLISTED_ARGS:
            # This parameter is related to the I/O directories which are already handled by the container
            continue
        elif param not in ALLOWED_ARGS:
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

    target_dir = prepare_target_project()
    command = build_command(argv, target_dir)
    os.execvp(command[0], command)