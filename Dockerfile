# Use Debian-based JDK image
FROM openjdk:11-jdk-slim

# Install required dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    unzip \
    git \
    wget \
    zip \
    apt-utils \
    && rm -rf /var/lib/apt/lists/*

# Install Android SDK command line tools
ENV ANDROID_SDK_ROOT=/sdk

RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    curl -o /tmp/commandlinetools.zip https://dl.google.com/android/repository/commandlinetools-linux-9123335_latest.zip && \
    unzip /tmp/commandlinetools.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools && \
    rm /tmp/commandlinetools.zip && \
    mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest

# Update PATH
ENV PATH=${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools:${PATH}

# Accept licenses
RUN yes | sdkmanager --licenses

# Install essential SDK packages
RUN sdkmanager \
    "platform-tools" \
    "platforms;android-33" \
    "build-tools;33.0.2"

# Set workdir for your app
WORKDIR /app

# Copy your project files into the container
COPY . .

# Download Gradle Wrapper if not included
RUN ./gradlew --version || (wget https://services.gradle.org/distributions/gradle-7.6-bin.zip -P /tmp && unzip /tmp/gradle-7.6-bin.zip -d /opt && ln -s /opt/gradle-7.6/bin/gradle /usr/bin/gradle)

# Build APK
RUN ./gradlew clean assembleDebug

CMD ["./gradlew", "assembleDebug"]

