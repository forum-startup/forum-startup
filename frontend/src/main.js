import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from "./router/router.js";
import {initializeApp} from "./utils/boot.js";

await initializeApp()

createApp(App).use(router).mount('#app')
