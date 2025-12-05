<script setup>
import {ref, watch, onMounted} from 'vue'
import {useUsers} from '../composables/useUsers.js'
import {debounce} from 'lodash-es'

const {users, isLoading, error, fetchUsers, blockUser, unblockUser, promoteToAdmin} = useUsers()

const searchQuery = ref('')
const isSearching = ref(false)

const performSearch = debounce(async (query) => {
  isSearching.value = true
  try {
    await fetchUsers(
        0,
        10,
        'username,asc',
        query || undefined,
        query || undefined,
        query || undefined
    )
  } finally {
    isSearching.value = false
  }
}, 300)

// Watch search input
watch(searchQuery, (newQuery) => {
      if (newQuery.trim() === '') {
        fetchUsers()
      } else
        performSearch(newQuery.trim())
    }
)

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="flex-1 px-6 py-12">
    <div class="w-full max-w-7xl mx-auto">

      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold tracking-tight text-white">User Management</h1>
        <p class="mt-3 text-lg text-gray-400">Search and manage all registered users</p>
      </div>

      <div class="max-w-2xl mx-auto mb-10">
        <div class="relative group">
          <div class="absolute -inset-1 bg-gradient-to-r rounded-2xl blur-xl opacity-60 group-focus-within:opacity-100 transition duration-700"></div>
          <div class="relative flex items-center rounded-2xl bg-white/5 backdrop-blur-xl border border-white/10 shadow-2xl">
            <svg class="absolute left-5 w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
            </svg>
            <input
                v-model="searchQuery"
                type="text"
                placeholder="Search by username, email, or first name..."
                class="w-full h-16 pl-14 pr-6 bg-transparent text-white placeholder-gray-500 focus:outline-none text-lg font-medium"
                @keydown.esc="searchQuery = ''"
            />
            <div v-if="isSearching" class="pr-5">
              <div class="w-5 h-5 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin"></div>
            </div>
          </div>
        </div>
        <p class="text-center mt-4 text-sm text-gray-500">
          {{ searchQuery ? `Searching for "${searchQuery}"...` : 'All users' }}
        </p>
      </div>

      <!-- Loading -->
      <div v-if="isLoading && !isSearching" class="space-y-4">
        <div v-for="n in 8" :key="n" class="animate-pulse">
          <div class="h-24 rounded-2xl bg-white/5"></div>
        </div>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="text-center py-16">
        <p class="text-red-400 font-medium text-xl">{{ error }}</p>
        <button @click="fetchUsers"
                class="mt-6 px-6 py-3 bg-red-900/30 text-red-300 rounded-xl hover:bg-red-900/50 transition">
          Try Again
        </button>
      </div>

      <!-- Users List -->
      <div v-else class="rounded-2xl bg-white/5 backdrop-blur-xl border border-white/10 shadow-2xl overflow-hidden">
        <div class="px-8 py-6 border-b border-white/10 flex items-center justify-between">
          <h2 class="text-2xl font-semibold text-white">
            Users <span class="text-indigo-400">({{ users.length }})</span>
          </h2>
          <button @click="fetchUsers" class="text-gray-400 hover:text-white transition text-sm">
            Refresh
          </button>
        </div>

        <ul class="divide-y divide-white/10">
          <li v-for="user in users" :key="user.id" class="hover:bg-white/5 transition duration-200">
            <div class="px-8 py-6 flex items-center justify-between">
              <div class="flex items-center gap-5">
                <!-- Avatar -->
                <div
                    class="h-14 w-14 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold text-xl shadow-lg">
                  {{ user.username[0].toUpperCase() }}
                </div>

                <!-- Info -->
                <div>
                  <div class="flex items-center gap-3">
                    <p class="text-lg font-medium text-white">@{{ user.username }}</p>
                    <span v-if="user.isBlocked"
                          class="px-3 py-1 rounded-full text-xs font-medium bg-red-900/50 text-red-300">
                      Blocked
                    </span>
                    <span v-if="user.roles?.some(r => r.name === 'ROLE_ADMIN')"
                          class="px-3 py-1 rounded-full text-xs font-medium bg-indigo-900/50 text-indigo-300">
                      Admin
                    </span>
                  </div>
                  <p class="text-sm text-gray-400 mt-1">{{ user.email }}</p>
                  <p class="text-xs text-gray-500 mt-2">
                    Joined {{ new Date(user.createdAt).toLocaleDateString() }} • {{ user.postsCount || 0 }} posts
                  </p>
                </div>
              </div>

              <!-- Actions -->
              <div class="flex items-center gap-3">
                <button
                    v-if="!user.isBlocked"
                    @click="promoteToAdmin(user.id)"
                    class="px-5 py-2.5 text-sm font-medium text-orange-400 hover:bg-orange-900/50 rounded-xl transition"
                >
                  Promote
                </button>
                <button
                    :class="user.isBlocked ? ' text-green-400 hover:bg-green-900/50' : ' text-red-400 hover:bg-red-900/50'"
                    @click="user.isBlocked ? unblockUser(user.id) : blockUser(user.id)"
                    class="px-5 py-2.5 text-sm font-medium rounded-xl transition"
                >
                  {{ user.isBlocked ? 'Unblock' : 'Block' }}
                </button>
              </div>
            </div>
          </li>
        </ul>

        <!-- Empty State -->
        <div v-if="users.length === 0" class="text-center py-20 text-gray-500">
          <p class="text-xl">No users found</p>
        </div>
      </div>
    </div>
  </div>
</template>