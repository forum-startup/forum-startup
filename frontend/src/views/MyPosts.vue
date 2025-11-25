<script setup>
import {usePosts} from "../composables/usePosts.js";
import {onMounted} from "vue";
import {currentUser, userLoading} from "../utils/store.js";

const {posts, isLoading, error, fetchCurrentUserPosts, editPostById, deletePostById} = usePosts()

onMounted(() => {
  console.log(currentUser.value)
  if (currentUser.value) {
    fetchCurrentUserPosts()
  }
})

</script>

<template>
  <div class="flex-1 flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-5xl">

      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold tracking-tight text-white">
          My Posts
        </h1>
        <p class="mt-3 text-lg text-gray-400">
          View and manage all your posts
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
        <button @click="fetchCurrentUserPosts" class="mt-4 text-indigo-400 hover:text-indigo-300 text-sm">
          Try again
        </button>
      </div>

      <!-- Posts Table (Card Style) -->
      <div v-else class="rounded-2xl bg-gray-800/50 backdrop-blur-sm ring-1 ring-white/10 shadow-2xl overflow-hidden">
        <div class="px-8 py-6 border-b border-white/10">
          <div class="flex items-center justify-between">
            <h2 class="text-2xl font-semibold text-white">
              All Posts ({{ posts.length }})
            </h2>
            <button
                @click="fetchCurrentUserPosts"
                class="text-sm text-gray-400 hover:text-white transition"
            >
              Refresh
            </button>
          </div>
        </div>

        <!-- Posts List -->
        <ul class="divide-y divide-white/10">
          <li v-for="post in posts" :key="post.id" class="hover:bg-white/5 transition">
            <div class="px-8 py-6 flex items-center justify-between">
              <!-- Left: Post Info -->
              <div class="flex items-center gap-5">
                <!-- Details -->
                <div>
                  <div class="flex items-center gap-3">
                    <p class="text-lg font-medium text-white">{{ post.title }}</p>
                  </div>
                  <div class="flex items-center gap-4 mt-2 text-xs text-gray-500">
                    <span>Posted {{ new Date(post.createdAt).toLocaleDateString() }}</span>
                    <span>•</span>
                    <span>{{ post.likesCount || 0 }} likes</span>
                    <span>•</span>
                    <span>{{ post.likesCount || 0 }} Fix this! comments</span>
                  </div>
                </div>
              </div>

              <div class="flex items-center gap-3 w-auto">
                <button
                    @click="editPostById(post.id)"
                    class="px-4 py-2 text-sm font-medium text-blue-400 hover:bg-blue-900/70 rounded-lg transition mr-2"
                >
                  Edit
                </button>
                <button
                    @click="deletePostById(post.id)"
                    class="px-4 py-2 text-sm font-medium text-red-400 hover:bg-red-900/30 rounded-lg transition mr-2"
                >
                  Delete
                </button>
              </div>
            </div>
          </li>
        </ul>

        <!-- Empty State -->
        <div v-if="posts.length === 0" class="text-center py-16">
          <p class="text-gray-400">No posts found.</p>
        </div>
      </div>
    </div>
  </div>
</template>