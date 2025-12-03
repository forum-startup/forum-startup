import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from "./router/router.js";
import {initializeApp} from "./utils/boot.js";
import 'vue3-toastify/dist/index.css';
import {toast} from "vue3-toastify";

await initializeApp()

const app = createApp(App)
app.use(router)

app.provide('toast', toast)
app.mount('#app')