# Step 1: Build the project
FROM ubuntu:latest AS builder

# Install Java 21 and Java 21 Debug and sbt and verilator
RUN apt-get update && \
    apt-get install apt-transport-https curl gnupg make -yqq && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import && \
    chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg && \
    apt-get update && \
    apt-get install -y openjdk-21-jdk openjdk-21-dbg sbt verilator git help2man perl python3 make autoconf g++ flex bison ccache libgoogle-perftools-dev numactl perl-doc libfl2 libfl-dev zlib1g zlib1g-dev

WORKDIR /app

# Copy the build definition files
COPY sources/ .

# Download and cache the project dependencies
RUN sbt compile
RUN sbt "Test / compile"
RUN sbt copyDependencies

# Step 2: Create the final image
FROM ubuntu:latest

# Install Java 21 and Java 21 Debug and sbt and verilator
RUN apt-get update && \
    apt-get install apt-transport-https curl gnupg make -yqq && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import && \
    chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg && \
    apt-get update && \
    apt-get install -y openjdk-21-jdk openjdk-21-dbg sbt verilator git help2man perl python3 make autoconf g++ flex bison ccache libgoogle-perftools-dev numactl perl-doc libfl2 libfl-dev zlib1g zlib1g-dev


WORKDIR /app

# Copy the built artifacts from the previous step
COPY --from=builder /app/target/dependency-jars/ .

# Set the entry point for the application
# ENTRYPOINT ["java",  "-cp",  "'*'",  "org.scalatest.run InteractiveTester"]