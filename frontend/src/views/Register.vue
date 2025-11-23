<script setup>
import { useRegisterForm } from '../composables/useRegisterForm'

const {
  form,
  errors,
  serverError,
  isLoading,
  register
} = useRegisterForm()
</script>

<template>
  <div class="flex min-h-[840px] flex-col bg-gray-900">
    <div class="flex min-h-full flex-1 flex-col justify-center px-6 py-12 lg:px-8">

      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-white">Become part of our community</h2>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <form class="space-y-6" @submit.prevent="register">
          <div>
            <label for="firstName" class="block text-sm font-medium leading-6 text-white">First Name</label>
            <input id="firstName" type="text" v-model="form.firstName" required
                   class="block w-full rounded-md border-0 py-1.5 text-white shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6">
          </div>

          <div>
            <label for="lastName" class="block text-sm font-medium leading-6 text-white">Last Name</label>
            <input id="lastName" type="text" v-model="form.lastName" required
                   class="block w-full rounded-md border-0 py-1.5 text-white shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6">
          </div>

          <div>
            <label for="email" class="block text-sm font-medium leading-6 text-white">Email</label>
            <input id="email" type="email" v-model="form.email" required
                   class="block w-full rounded-md border-0 py-1.5 text-white shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6">
          </div>

          <div>
            <label for="username" class="block text-sm font-medium leading-6 text-white">Username</label>
            <input id="username" type="text" v-model="form.username" required
                   class="block w-full rounded-md border-0 py-1.5 text-white shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6">
          </div>

          <div>
            <label for="password" class="block text-sm font-medium leading-6 text-white">Password</label>
            <input id="password" type="password" v-model="form.password" required
                   class="block w-full rounded-md border-0 py-1.5 text-white shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6">
          </div>

          <button type="submit" class="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500">
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
  </div>
</template>