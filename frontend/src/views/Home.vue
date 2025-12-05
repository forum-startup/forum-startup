<script setup>
import {onMounted, computed, ref, watch} from "vue";
import {usePosts} from "../composables/usePosts.js";
import {usePost} from "../composables/usePost.js";
import {useRouter} from "vue-router";
import {currentUser} from "../utils/store.js";
import {useUsers} from "../composables/useUsers.js";
import {debounce} from "lodash-es";

const {
  posts,
  count: postCount,
  fetchTotalPostCount,
  fetchRecentPosts,
  fetchAllPosts,
  filterPosts,
} = usePosts();

const {
  count: userCount,
  fetchTotalUserCount,
} = useUsers()

const {toggleLike} = usePost();
const router = useRouter();

const isLoggedIn = computed(() => !!currentUser.value);
const username = computed(() => currentUser.value?.username ?? "");

const searchQuery = ref('')
const isSearching = ref(false)

const performSearch = debounce(async (query) => {
  isSearching.value = true
  try {
    await filterPosts(
        0,
        10,
        'createdAt,desc',
        query || undefined,
        query || undefined,
        query || undefined
    )
  } finally {
    isSearching.value = false
  }
}, 300)

watch(searchQuery, (newQuery) => {
      if (newQuery.trim() === '') {
        filterPosts()
      } else
        performSearch(newQuery.trim())
    }
)

onMounted(async () => {
  await fetchTotalPostCount()
  await fetchTotalUserCount()

  if (currentUser.value) {
    await filterPosts();
  } else {
    await fetchRecentPosts();
  }
});

function navigateToPost(postId) {
  if (!currentUser.value) return;
  router.push({name: "Post", params: {postId}});
}

function navigateToUser(userId) {
  if (!currentUser.value) return;
  router.push({name: "UserProfile", params: {userId}});
}

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}
</script>

<template>
  <div class="min-h-screen bg-gray-900 pt-20 pb-32">
    <div class="mx-auto max-w-7xl px-6 lg:px-8">

      <!-- Header + Search + Stats -->
      <div class="mb-20 space-y-16 text-center">

        <!-- 1. Guest View -->
        <div v-if="!isLoggedIn">
          <h1 class="text-5xl sm:text-6xl font-bold text-white leading-tight">
            Latest Posts
          </h1>
          <p class="mt-6 text-xl text-gray-400 max-w-2xl mx-auto">
            The newest ideas and discussions from our growing community.
          </p>
          <!-- Stats Cards -->
          <div class="mt-10 grid grid-cols-1 sm:grid-cols-2 gap-8 max-w-4xl mx-auto">
            <!-- Community Card -->
            <div
                class="group relative overflow-hidden rounded-3xl bg-white/5 backdrop-blur-xl border border-white/10 p-10 shadow-2xl hover:shadow-cyan-500/30 hover:border-cyan-500/40 transition-all duration-500">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-bold text-cyan-400 uppercase tracking-wider">Community</p>
                  <p class="mt-5 text-6xl font-black text-white">{{ userCount }}</p>
                  <p class="mt-3 text-gray-400">Active members</p>
                </div>
                <div
                    class="w-24 h-24 rounded-3xl bg-gradient-to-br from-cyan-500 to-blue-600 flex items-center justify-center shadow-2xl">
                  <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/>
                  </svg>
                </div>
              </div>
              <div
                  class="absolute inset-x-0 bottom-0 h-2 bg-gradient-to-r from-cyan-500 to-blue-600 scale-x-0 group-hover:scale-x-100 transition-transform duration-700 origin-left"></div>
            </div>

            <!-- Posts Card -->
            <div
                class="group relative overflow-hidden rounded-3xl bg-white/5 backdrop-blur-xl border border-white/10 p-10 shadow-2xl hover:shadow-purple-500/30 hover:border-purple-500/40 transition-all duration-500">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm font-bold text-purple-400 uppercase tracking-wider">Content</p>
                  <p class="mt-5 text-6xl font-black text-white">{{ postCount }}</p>
                  <p class="mt-3 text-gray-400">Posts shared</p>
                </div>
                <div
                    class="w-24 h-24 rounded-3xl bg-gradient-to-br from-purple-500 to-pink-600 flex items-center justify-center shadow-2xl">
                  <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                  </svg>
                </div>
              </div>
              <div
                  class="absolute inset-x-0 bottom-0 h-2 bg-gradient-to-r from-purple-500 to-pink-600 scale-x-0 group-hover:scale-x-100 transition-transform duration-700 origin-left"></div>
            </div>
          </div>
        </div>

        <!-- 2. Logged-in: Welcome Message -->
        <div v-else>
          <h1 class="text-5xl sm:text-6xl font-bold text-white leading-tight">
            Welcome back,<br class="hidden sm:inline"/>
            <span class="bg-gradient-to-r from-indigo-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
        @{{ username }}
      </span>
          </h1>
          <p class="mt-6 text-xl text-gray-400">
            Here's what's new in the community today
          </p>
        </div>

        <!--  Search Bar -->
        <div class="max-w-2xl mx-auto">
          <div class="relative group">
            <!-- Glow Effect -->
            <div
                class="absolute -inset-1 bg-gradient-to-r rounded-2xl blur-xl opacity-60 group-focus-within:opacity-100 transition duration-700"></div>

            <!-- Input -->
            <div
                class="relative flex items-center rounded-2xl bg-white/5 backdrop-blur-xl border border-white/10 shadow-2xl">
              <svg class="absolute left-6 w-7 h-7 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
              </svg>
              <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="Search posts, users, titles..."
                  class="w-full h-16 pl-16 pr-14 bg-transparent text-white placeholder-gray-500 focus:outline-none text-lg font-medium"
                  @keydown.esc="searchQuery = ''"
              />
              <div v-if="isSearching" class="absolute right-5">
                <div class="w-6 h-6 border-3 border-indigo-400 border-t-transparent rounded-full animate-spin"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Divider -->
      <hr class="h-px bg-gradient-to-r from-transparent via-white/20 to-transparent border-0"/>

      <!-- Posts Grid -->
      <div class="mt-20">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          <article
              v-for="post in posts"
              :key="post.postId"
              class="group relative h-full flex flex-col rounded-3xl bg-[#0f172a]/90 backdrop-blur-sm border border-white/10 shadow-2xl hover:shadow-indigo-500/20 transition-all duration-500 hover:-translate-y-3 overflow-hidden"
          >
            <!-- Fixed layout using grid -->
            <div class="grid grid-rows-[auto_1fr_auto] h-full">
              <!-- Top: Date + Badge -->
              <div class="p-8 pb-4">
                <div class="flex items-center justify-between text-xs text-gray-500">
                  <time>{{ formatDate(post.createdAt) }}</time>
                  <span class="px-3 py-1 rounded-full bg-indigo-900/30 text-indigo-300 font-medium">Post</span>
                </div>
              </div>

              <!-- Middle: Title + Content (fixed height) -->
              <div @click="navigateToPost(post.postId)" class="cursor-pointer px-8 pb-6 space-y-5">
                <!-- Title: Always takes 2 lines of space -->
                <h3 class="text-2xl font-bold text-white line-clamp-2 group-hover:text-indigo-300 transition-colors min-h-[3.5rem] leading-tight">
                  {{ post.title }}
                </h3>

                <!-- Content: Always 3 lines -->
                <p class="text-gray-400 line-clamp-3 leading-relaxed min-h-[4.5rem]">
                  {{ post.content }}
                </p>
              </div>

              <!-- Bottom: Footer (always sticks to bottom) -->
              <div class="px-8 py-6 border-t border-white/10 bg-gradient-to-t from-black/20 to-transparent">
                <div class="flex items-center justify-between">
                  <button
                      @click.stop="navigateToUser(post.creatorId)"
                      :disabled="!isLoggedIn"
                      class="flex items-center gap-3 hover:gap-4 transition-all disabled:opacity-50"
                  >
                    <div
                        class="w-11 h-11 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold text-lg shadow-xl">
                      {{ post.creatorUsername[0].toUpperCase() }}
                    </div>
                    <span class="font-medium text-gray-300">@{{ post.creatorUsername }}</span>
                  </button>

                  <button
                      @click.stop="toggleLike(post)"
                      :disabled="!isLoggedIn"
                      class="flex items-center gap-2.5 px-4 py-2.5 rounded-xl transition-all hover:scale-110"
                      :class="post.likedByCurrentUser
            ? 'bg-red-500/20 text-red-400'
            : 'text-gray-400 hover:text-red-400 hover:bg-red-500/10'"
                  >
                    <svg class="w-5 h-5" :fill="post.likedByCurrentUser ? 'currentColor' : 'none'" stroke="currentColor"
                         viewBox="0 0 20 20">
                      <path fill-rule="evenodd"
                            d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z"
                            clip-rule="evenodd"/>
                    </svg>
                    <span class="font-bold text-sm">{{ post.likesCount || 0 }}</span>
                  </button>
                </div>
              </div>
            </div>

            <!-- Hover glow -->
            <div
                class="pointer-events-none absolute inset-0 rounded-3xl ring-1 ring-inset ring-white/0 group-hover:ring-indigo-400/30 transition duration-500"></div>
          </article>
        </div>

        <!-- Empty State -->
        <div v-if="posts.length === 0" class="text-center py-20">
          <p class="text-2xl text-gray-500">No posts yet.</p>
          <p class="text-gray-400 mt-2">Be the first to share something!</p>
        </div>
      </div>
    </div>
  </div>
</template>