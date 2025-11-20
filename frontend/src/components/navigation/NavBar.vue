<script setup>
import {ref} from 'vue'
import logo from '../../assets/startup-logo-white.png'

const mobileMenuOpen = ref(false)

function closeMenu() {
  mobileMenuOpen.value = false
}

function toggleMenu() {
  mobileMenuOpen.value = !mobileMenuOpen.value
}
</script>

<template>
  <div class="bg-white">

    <header class="bg-gray-800">
      <nav class="mx-auto flex max-w-7xl items-center justify-between gap-x-6 p-6 lg:px-8" aria-label="Global">

        <!-- Logo -->
        <div class="flex lg:flex-1">
          <a href="../../views/Home.vue" class="-m-1.5 p-1.5">
            <span class="sr-only">StartUp</span>
            <img class="h-8 w-auto" :src="logo" alt="StartUp Logo">
          </a>
        </div>

        <!-- Desktop menu -->
        <div class="hidden lg:flex lg:gap-x-12">
          <a href="#" class="text-sm font-semibold leading-6 text-white">Product</a>
          <a href="#" class="text-sm font-semibold leading-6 text-white">Features</a>
          <a href="#" class="text-sm font-semibold leading-6 text-white">Marketplace</a>
          <a href="#" class="text-sm font-semibold leading-6 text-white">Company</a>
        </div>

        <!-- Login/Signup buttons -->
        <div class="flex flex-1 items-center justify-end gap-x-6">
          <a href="/login" class="hidden lg:block lg:text-sm lg:font-semibold lg:leading-6 lg:text-white">Log in</a>
          <a href="/register"
             class="rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600">Sign
            up</a>
        </div>

        <!-- Mobile menu button -->
        <div class="flex lg:hidden">
          <button type="button"
                  class="-m-2.5 inline-flex items-center justify-center rounded-md p-2.5 text-gray-700"
                  @click="toggleMenu">
            <span class="sr-only">Open main menu</span>
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"
                 aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round"
                    d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5"></path>
            </svg>
          </button>
        </div>
      </nav>

      <!-- Mobile menu -->
      <div v-if="mobileMenuOpen" class="lg:hidden fixed inset-0 z-10 bg-white p-6 overflow-y-auto">
        <div class="flex items-center justify-between mb-6">
          <a href="/" class="-m-1.5 p-1.5">
            <span class="sr-only">Forum</span>
            <img class="h-8 w-auto" :src="logo" alt="Logo"/>
          </a>
          <button @click="closeMenu" type="button" class="rounded-md p-2.5 text-gray-700">
            <span class="sr-only">Close menu</span>
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"
                 aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <div class="space-y-4">
          <a href="#" class="block text-base font-semibold text-white hover:bg-gray-50 px-3 py-2 rounded-lg">Product</a>
          <a href="#"
             class="block text-base font-semibold text-white hover:bg-gray-50 px-3 py-2 rounded-lg">Features</a>
          <a href="#"
             class="block text-base font-semibold text-white hover:bg-gray-50 px-3 py-2 rounded-lg">Marketplace</a>
          <a href="#" class="block text-base font-semibold text-white hover:bg-gray-50 px-3 py-2 rounded-lg">Company</a>
          <a href="/login" class="block text-base font-semibold text-white hover:bg-gray-50 px-3 py-2 rounded-lg">Log
            in</a>
        </div>
      </div>
    </header>

  </div>
</template>

<style scoped>

</style>