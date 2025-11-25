import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from "./router/router.js";
import {initializeApp} from "./utils/boot.js";

// loads current user (since a refresh looses local variables)
await initializeApp()

createApp(App).use(router).mount('#app')
