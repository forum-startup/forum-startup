<script setup>
import {computed, ref} from 'vue'
import {onClickOutside} from '@vueuse/core'
import {useRouter} from 'vue-router'
import {currentUser, userLoading} from '../utils/store.js'
import {hasRoleReactive} from "../utils/auth.js";
import {logout} from '../utils/auth.js'
import logo from '../assets/startup-logo-white.png'

// Reactive state
const isProfileOpen = ref(false)
const dropdownRef = ref(null)
const router = useRouter()
const isAdmin = computed(() => hasRoleReactive('ROLE_ADMIN'))

// Close dropdown when clicking outside
onClickOutside(dropdownRef, () => (isProfileOpen.value = false))

async function handleLogout() {
  await logout()
  isProfileOpen.value = false
  router.push('/')
}
</script>

<template>
  <header class="bg-gray-800 sticky top-0 z-50 border-b border-white/5">
    <nav class="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">

      <!-- Logo -->
      <router-link to="/" class="flex items-center -m-2 p-2 rounded-lg hover:bg-white/5 transition">
        <img class="h-9 w-auto" :src="logo" alt="StartUp Logo"/>
        <span class="sr-only">StartUp Home</span>
      </router-link>

      <!-- Desktop Navigation -->
      <div class="hidden lg:flex lg:items-center lg:gap-x-10">
        <router-link
            v-if="currentUser"
            to="/post"
            class="text-sm font-medium text-white hover:text-indigo-400 transition"
        >
          Share your story
        </router-link>
      </div>

      <!-- Right Side: Auth Controls -->
      <div class="flex items-center gap-x-4">

        <!-- Loading State -->
        <div v-if="userLoading" class="flex items-center gap-x-4">
          <div class="h-9 w-20 animate-pulse rounded-lg bg-gray-700"></div>
          <div class="h-9 w-24 animate-pulse rounded-lg bg-gray-700"></div>
        </div>

        <!-- Guest (Not Logged In) -->
        <template v-else-if="!currentUser">
          <router-link
              to="/login"
              class="hidden sm:block text-sm font-medium text-gray-300 hover:text-white transition"
          >
            Log in
          </router-link>
          <router-link
              to="/register"
              class="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm
                   hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2
                   focus-visible:outline-offset-2 focus-visible:outline-indigo-500 transition"
          >
            Sign up
          </router-link>
        </template>

        <!-- Authenticated User -->
        <div v-else ref="dropdownRef" class="relative">
          <!-- Profile Button -->
          <button
              @click="isProfileOpen = !isProfileOpen"
              class="flex items-center gap-3 rounded-xl bg-gray-700/70 px-4 py-2.5
                   text-sm font-medium text-white ring-1 ring-white/20
                   hover:bg-gray-600/80 focus-visible:outline focus-visible:outline-2
                   focus-visible:outline-offset-2 focus-visible:outline-indigo-500
                   transition-all duration-200"
              aria-expanded="isProfileOpen"
              aria-haspopup="true"
          >
            <span class="hidden sm:inline">@{{ currentUser.username }}</span>
            <span class="sm:hidden">Menu</span>
            <svg
                class="h-5 w-5 text-gray-400 transition-transform duration-200"
                :class="{ 'rotate-180': isProfileOpen }"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
            </svg>
          </button>

          <!-- Dropdown Menu -->
          <transition
              enter-active-class="transition ease-out duration-150"
              enter-from-class="transform opacity-0 scale-95 translate-y-1"
              enter-to-class="transform opacity-100 scale-100 translate-y-0"
              leave-active-class="transition ease-in duration-100"
              leave-from-class="transform opacity-100 scale-100 translate-y-0"
              leave-to-class="transform opacity-0 scale-95 translate-y-1"
          >
            <div
                v-show="isProfileOpen"
                class="absolute right-0 mt-3 w-64 origin-top-right overflow-hidden
                     rounded-xl bg-gray-800 shadow-2xl ring-1 ring-white/10
                     focus:outline-none"
                role="menu"
            >
              <!-- User Header -->
              <div class="border-b border-white/10 px-5 py-4">
                <p class="text-xs font-medium text-gray-400">Signed in as</p>
                <p class="truncate text-sm font-bold text-white">@{{ currentUser.username }}</p>
                <p class="truncate text-xs text-gray-400">{{ currentUser.email }}</p>
              </div>

              <!-- Menu Items -->
              <div class="py-2">
                <router-link
                    to="/profile"
                    @click="isProfileOpen = false"
                    class="block px-5 py-3 text-sm text-gray-300 hover:bg-gray-700/70 hover:text-white transition"
                    role="menuitem"
                >
                  My Account
                </router-link>
                <router-link
                    to="/my-posts"
                    @click="isProfileOpen = false"
                    class="block px-5 py-3 text-sm text-gray-300 hover:bg-gray-700/70 hover:text-white transition"
                    role="menuitem"
                >
                  My Posts
                </router-link>
                <div v-if="isAdmin">
                  <router-link
                      to="/users"
                      @click="isProfileOpen = false"
                      class="block px-5 py-3 text-sm text-gray-300 hover:bg-gray-700/70 hover:text-white transition"
                      role="menuitem"
                  >
                    Browse Users
                  </router-link>
                </div>
              </div>

              <!-- Logout -->
              <div class="border-t border-white/10 py-2">
                <button
                    @click="handleLogout"
                    class="w-full px-5 py-3 text-left text-sm font-medium text-red-400
                         hover:bg-red-900/30 hover:text-red-300 transition"
                    role="menuitem"
                >
                  Log out
                </button>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </nav>
  </header>
</template>