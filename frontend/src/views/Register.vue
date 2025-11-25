<script setup>
import {useRegisterForm} from '../composables/useRegisterForm'

const {
  form,
  errors,
  serverError,
  isLoading,
  register
} = useRegisterForm()
</script>

<template>
  <div class="flex-1 flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-md space-y-8">
      <div class="text-center">
        <h2 class="text-3xl font-bold text-white">Become part of our community</h2>
      </div>

      <form class="space-y-6" @submit.prevent="register">
        <div>
          <label for="firstName" class="block text-sm font-medium text-gray-300">First Name</label>
          <input v-model="form.firstName" placeholder="Your first name" required class="mt-1 block w-full rounded-md border-0 bg-white/10 py-2.5 px-3 text-white placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500" />
        </div>

        <div>
          <label for="lastName" class="block text-sm font-medium text-gray-300">Last Name</label>
          <input v-model="form.lastName" placeholder="Your last name" required class="mt-1 block w-full rounded-md border-0 bg-white/10 py-2.5 px-3 text-white placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500" />
        </div>

        <div>
          <label for="email" class="block text-sm font-medium text-gray-300">Email</label>
          <input type="email" placeholder="example@example.com" v-model="form.email" required
                 class="mt-1 block w-full rounded-md border-0 bg-white/10 py-2.5 px-3 text-white placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500" />
        </div>

        <div>
          <label for="username" class="block text-sm font-medium text-gray-300">Username</label>
          <input type="text" placeholder="Your username" v-model="form.username" required
                 class="mt-1 block w-full rounded-md border-0 bg-white/10 py-2.5 px-3 text-white placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500" />
        </div>

        <div>
          <label for="password" class="block text-sm font-medium text-gray-300">Password</label>
          <input type="password"v-model="form.password" required
                 class="mt-1 block w-full rounded-md border-0 bg-white/10 py-2.5 px-3 text-white placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500" />
        </div>

        <button type="submit" class="w-full rounded-md bg-indigo-600 py-3 text-white font-semibold hover:bg-indigo-500">
          Register
        </button>

        <!-- Fix error display -->
        <div v-if="Object.keys(errors).length > 0" class="space-y-2">
          <p v-for="(message, field) in errors" :key="field" class="text-red-400 text-sm">
            {{ message }}
          </p>
        </div>

        <p v-if="serverError" class="text-red-500 text-sm mt-4 text-center font-medium">
          {{ serverError }}
        </p>
      </form>
    </div>
  </div>
</template>