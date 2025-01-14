package data

val sampleOpenApiSpec = """
    openapi: 3.0.0
    info:
      title: Example API
      version: 1.0.0
    paths:
      /hello:
        get:
          responses:
            '200':
              description: A simple hello world
              content:
                text/plain:
                  schema:
                    type: string
    components:
      schemas:
        Pet:
          type: object
          required:
            - name
            - type
          properties:
            name:
              type: string
            type:
              type: string
              enum:
                - dog
                - cat
            age:
              type: integer
    """.trimIndent()
