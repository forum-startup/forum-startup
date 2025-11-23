<script setup>
import { ref } from 'vue'
import { onClickOutside } from '@vueuse/core'
import { currentUser } from '../utils/store.js'
import router from '../router/router.js'
import logo from '../assets/startup-logo-white.png'
import { logout } from '../utils/auth.js'

const isOpen = ref(false)
const dropdownTriggerRef = ref(null)

onClickOutside(dropdownTriggerRef, () => {
  isOpen.value = false
})

async function handleLogout() {
  await logout()
  isOpen.value = false
  await router.push('/')
}

function toggleDropdown() {
  isOpen.value = !isOpen.value
}
</script>

<template>
  <header class="bg-gray-800 sticky top-0 z-50">
    <nav class="mx-auto flex max-w-7xl items-center justify-between gap-x-6 p-6 lg:px-8">
      <!-- Logo -->
      <router-link to="/" class="-m-1.5 p-1.5 flex lg:flex-1">
        <img class="h-8 w-auto" :src="logo" alt="StartUp Logo" />
      </router-link>

      <!-- Desktop Links -->
      <div class="hidden lg:flex lg:gap-x-12">
        <router-link
            to="/post"
            class="hidden lg:block text-sm font-semibold leading-6 text-white"
        >
          Share your story
        </router-link>
      </div>

      <!-- Auth Section -->
      <div class="flex flex-1 items-center justify-end gap-x-6">
        <!-- Not logged in -->
        <template v-if="!currentUser">
          <router-link
              to="/login"
              class="hidden lg:block text-sm font-semibold leading-6 text-white"
          >
            Log in
          </router-link>
          <router-link
              to="/register"
              class="rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500"
          >
            Sign up
          </router-link>
        </template>

        <!-- Logged in → Profile Dropdown -->
        <template v-else>
          <div ref="dropdownTriggerRef" class="relative">
            <!-- Trigger Button -->
            <button
                type="button"
                @click="toggleDropdown"
                class="flex items-center gap-x-2 rounded-md bg-gray-700 px-4 py-2 text-sm font-semibold text-white ring-1 ring-white/20 hover:bg-white/20 transition"
            >
              <span>Profile</span>
              <svg
                  class="h-5 w-5 text-gray-300 transition-transform duration-200"
                  :class="{ 'rotate-180': isOpen }"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            <!-- Dropdown Panel -->
            <transition
                enter-active-class="transition ease-out duration-100"
                enter-from-class="transform opacity-0 scale-95"
                enter-to-class="transform opacity-100 scale-100"
                leave-active-class="transition ease-in duration-75"
                leave-from-class="transform opacity-100 scale-100"
                leave-to-class="transform opacity-0 scale-95"
            >
              <div
                  v-show="isOpen"
                  class="absolute right-0 mt-2 w-56 origin-top-right rounded-md bg-gray-700 shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none"
              >
                <!-- User Info -->
                <div class="px-4 py-3 border-b border-gray-500">
                  <p class="text-sm text-white">Signed in as</p>
                  <p class="truncate text-sm text-white font-semibold">
                    @{{ currentUser.username }}
                  </p>
                </div>

                <!-- Links -->
                <div class="py-1">
                  <router-link
                      to="/profile"
                      @click="isOpen = false"
                      class="block px-4 py-2 text-sm text-white hover:bg-gray-600"
                  >
                    My Account
                  </router-link>
                </div>

                <div class="py-1">
                  <router-link
                      to="/posts"
                      @click="isOpen = false"
                      class="block px-4 py-2 text-sm text-white hover:bg-gray-600"
                  >
                    My Posts
                  </router-link>
                </div>

                <!-- Logout -->
                <div class="py-1 border-t border-gray-500">
                  <button
                      @click="handleLogout"
                      class="w-full text-left block px-4 py-2 text-sm font-semibold text-red-500 hover:bg-gray-600"
                  >
                    Log out
                  </button>
                </div>
              </div>
            </transition>
          </div>
        </template>
      </div>
    </nav>
  </header>
</template>