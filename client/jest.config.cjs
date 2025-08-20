// jest.config.cjs
const { defaults: tsjPreset } = require('ts-jest/presets');
const path = require('path');

module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom',
  globals: {
    'ts-jest': {
      tsconfig: 'tsconfig.test.json'
    }
  },
  moduleNameMapper: {
    '^~/(.*)$': path.join(__dirname, 'app', '$1') // adjust if src is not the folder name
  },
  setupFilesAfterEnv: ['<rootDir>/jest.setup.ts']
};
