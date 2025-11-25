<script setup>
import {computed, onMounted} from 'vue'
import {useUsers} from '../composables/useUsers.js'
import {fetchUserById, hasRoleReactive} from "../utils/auth.js";

const {users, isLoading, isBlocking, error, fetchUsers, blockUser, unblockUser, promoteToAdmin} = useUsers()
// const isAdmin = computed(() => hasRoleReactive('ROLE_ADMIN'))

onMounted(fetchUsers)
</script>

<template>
  <div class="flex-1 flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-5xl">

      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold tracking-tight text-white">
          User Management
        </h1>
        <p class="mt-3 text-lg text-gray-400">
          View and manage all registered users
        </p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="space-y-4">
        <div v-for="n in 6" :key="n" class="animate-pulse">
          <div class="h-20 rounded-xl bg-gray-800/50"></div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-12">
        <p class="text-red-400 font-medium">{{ error }}</p>
        <button @click="fetchUsers" class="mt-4 text-indigo-400 hover:text-indigo-300 text-sm">
          Try again
        </button>
      </div>

      <!-- Users Table (Card Style) -->
      <div v-else class="rounded-2xl bg-gray-800/50 backdrop-blur-sm ring-1 ring-white/10 shadow-2xl overflow-hidden">
        <div class="px-8 py-6 border-b border-white/10">
          <div class="flex items-center justify-between">
            <h2 class="text-2xl font-semibold text-white">
              All Users ({{ users.length }})
            </h2>
            <button
                @click="fetchUsers"
                class="text-sm text-gray-400 hover:text-white transition"
            >
              Refresh
            </button>
          </div>
        </div>

        <!-- Users List -->
        <ul class="divide-y divide-white/10">
          <li v-for="user in users" :key="user.id" class="hover:bg-white/5 transition">
            <div class="px-8 py-6 flex items-center justify-between">
              <!-- Left: User Info -->
              <div class="flex items-center gap-5">
                <!-- Avatar -->
                <div
                    class="h-14 w-14 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold text-xl shadow-lg">
                  {{ user.username[0].toUpperCase() }}
                </div>

                <!-- Details -->
                <div>
                  <div class="flex items-center gap-3">
                    <p class="text-lg font-medium text-white">@{{ user.username }}</p>
                    <span v-if="user.isBlocked"
                          class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-900/50 text-red-300">
                      Blocked
                    </span>
                    <span v-if="user.roles?.map(r => r.name).includes('ROLE_ADMIN')"
                          class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-900/50 text-indigo-300">
  Admin
</span>
                  </div>
                  <p class="text-sm text-gray-400 mt-1">
                    {{ user.email }}
                  </p>
                  <div class="flex items-center gap-4 mt-2 text-xs text-gray-500">
                    <span>Joined {{ new Date(user.createdAt).toLocaleDateString() }}</span>
                    <span>•</span>
                    <span>{{ user.postsCount || 0 }} posts</span>
                  </div>
                </div>
              </div>

              <div class="flex items-center gap-3 w-auto">
                <button
                    v-if="!user.isBlocked"
                    @click="promoteToAdmin(user.id)"
                    class="px-4 py-2 text-sm font-medium text-orange-500 hover:bg-orange-900/30 rounded-lg transition"
                >
                  Promote
                </button>
                <button
                    v-if="!user.isBlocked"
                    @click="blockUser(user.id)"
                    class="px-4 py-2 text-sm font-medium text-red-400 hover:bg-red-900/30 rounded-lg transition mr-2"
                >
                  Block
                </button>
                <button
                    v-else
                    @click="unblockUser(user.id)"
                    class="px-4 py-2 text-sm font-medium text-green-400 hover:bg-green-900/30 rounded-lg transition"
                >
                  Unblock
                </button>
              </div>
            </div>
          </li>
        </ul>

        <!-- Empty State -->
        <div v-if="users.length === 0" class="text-center py-16">
          <p class="text-gray-400">No users found.</p>
        </div>
      </div>
    </div>
  </div>
</template>