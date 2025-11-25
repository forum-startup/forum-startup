<script setup>
import { ref } from "vue";
import api from "../utils/axios.js";
import router from "../router/router.js";
import { currentUser } from "../utils/store.js";
import { fetchCurrentUser } from "../utils/auth.js";

const form = ref({
  username: '',
  password: ''
})

const error = ref("")

async function login() {
  try {
    error.value = ""
    await api.post("public/auth/login",  form.value )

    // set current user
    currentUser.value = await fetchCurrentUser()

    await router.push("/")
  } catch (e) {
    error.value = "Invalid credentials."
  }
}
</script>

<template>
  <div class="flex-1 flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-md space-y-8">
      <div class="text-center">
        <h2 class="text-3xl font-bold text-white">Sign in to your account</h2>
      </div>

      <form class="space-y-6" @submit.prevent="login">

        <div>
          <label class="block text-sm font-medium text-gray-300">Username</label>
          <input v-model="form.username" required class="mt-1 block w-full rounded-md border-0 bg-white/10 py-2.5 px-3 text-white placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-300">Password</label>
          <input v-model="form.password" type="password" required class="mt-1 block w-full rounded-md border-0 bg-white/10 py-2.5 px-3 text-white placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500" />
        </div>

        <button type="submit" class="w-full rounded-md bg-indigo-600 py-3 text-white font-semibold hover:bg-indigo-500">
          Sign in
        </button>

        <div class="text-center text-sm font-medium text-indigo-400
         underline underline-offset-4 decoration-indigo-300/50
         hover:decoration-indigo-300 hover:text-indigo-300
         transition-colors duration-200">
          <router-link
              to="/register"
          >
            Don't have a registration yet? Sign up here
          </router-link>
        </div>

        <p v-if="error" class="text-center text-sm text-red-400">{{ error }}</p>
      </form>
    </div>
  </div>
</template>