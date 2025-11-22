<script setup>
import { ref } from "vue";
import api from "../utils/axios.js";
import router from "../router/router.js";
import { currentUser } from "../utils/store.js";
import { fetchCurrentUser } from "../utils/auth.js";

const username = ref("")
const password = ref("")
const error = ref("")

async function login() {
  try {
    error.value = ""
    await api.post("public/auth/login", {
      username: username.value,
      password: password.value
    })

    // set current user
    currentUser.value = await fetchCurrentUser()

    await router.push("/")
  } catch (e) {
    error.value = "Invalid credentials."
  }
}
</script>

<template>
  <div class="flex min-h-[840px] flex-col bg-gray-900">
    <div class="flex min-h-full flex-1 flex-col justify-center px-6 py-12 lg:px-8">

      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-white">Sign in to your account</h2>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <form class="space-y-6" @submit.prevent="login">
          <div>
            <label for="username" class="block text-sm font-medium leading-6 text-white">Username</label>
            <input id="username" type="text" v-model="username" required
                   class="block w-full rounded-md border-0 py-1.5 text-white shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6">
          </div>

          <div>
            <label for="password" class="block text-sm font-medium leading-6 text-white">Password</label>
            <input id="password" type="password" v-model="password" required
                   class="block w-full rounded-md border-0 py-1.5 text-white shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6">
          </div>

          <button type="submit" class="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500">
            Sign in
          </button>

          <p v-if="error" class="text-red-500 text-sm mt-2 text-center">{{ error }}</p>
        </form>
      </div>
    </div>
  </div>
</template>