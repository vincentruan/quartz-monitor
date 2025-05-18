import { createProdMockServer } from 'vite-plugin-mock/client'
import mockModules from './index'

export function setupProdMockServer() {
  createProdMockServer([...mockModules])
} 