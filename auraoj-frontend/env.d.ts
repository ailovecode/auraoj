/// <reference types="vite/client" />

interface ProcessEnv {
  readonly NODE_ENV: string
  readonly BASE_URL: string
}

interface Process {
  env: ProcessEnv
}

declare const process: Process
