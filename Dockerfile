FROM maven:3.9.16-eclipse-temurin-26-alpine AS build

WORKDIR /astor
COPY . .
RUN --mount=type=cache,target=/root/.m2/repository mvn clean package -DskipTests -B

FROM eclipse-temurin:26 AS astor_image

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
    wget gnupg software-properties-common maven \
    && wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor -o /etc/apt/trusted.gpg.d/adoptium.gpg \
    && echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" > /etc/apt/sources.list.d/adoptium.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        temurin-8-jdk \
        temurin-11-jdk \
        temurin-17-jdk \
        temurin-25-jdk \
        temurin-26-jdk \
        # for the entrypoint
        python3 \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY --from=build /astor/target /astor/target
COPY docker-run.py docker-run.py
RUN chmod +x docker-run.py

ENTRYPOINT ["python3", "docker-run.py"]