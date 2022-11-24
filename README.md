# Sidekick Test Module
Sidekick Test Module is an external Sidekick client that collects results from broker and serves them via REST API. Enabling usage of Sidekick stack & error collection data within test frameworks.

## Usage

- Edit application.yml under 'src/main/resources' to fit your needs. 
> If you are a user of Sidekick Cloud than you should use
    host: wss://broker.runsidekick.com &
    port: 443
    as your host & port values.

- Run & you will be able to start using Sidekick Test Module

- Checkout http://localhost:80/swagger-ui/index.html for api doc