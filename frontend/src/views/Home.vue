<script setup>
import { onMounted, computed } from "vue";
import { usePosts } from "../composables/usePosts.js";
import { usePost } from "../composables/usePost.js";
import { useRouter } from "vue-router";
import { currentUser } from "../utils/store.js";

const { posts, fetchRecentPosts, fetchAllPosts } = usePosts();
const { toggleLike } = usePost();
const router = useRouter();

const isLoggedIn = computed(() => !!currentUser.value);
const username = computed(() => currentUser.value?.username ?? "");

onMounted(async () => {
  if (currentUser.value) {
    await fetchAllPosts();
  } else {
    await fetchRecentPosts();
  }
});

function navigateToPost(postId) {
  if (!currentUser.value) return;
  router.push({ name: "Post", params: { postId } });
}

function navigateToUser(userId) {
  if (!currentUser.value) return;
  router.push({ name: "UserProfile", params: { userId } });
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
  <div class="bg-gray-900 pt-24 pb-32 flex-1">
    <div class="mx-auto max-w-7xl px-6 lg:px-8">
      <div class="mx-auto max-w-2xl lg:mx-0">
        <div v-if="!isLoggedIn">
          <h2 class="text-4xl sm:text-5xl font-semibold text-white">Latest Posts</h2>
          <p class="mt-2 text-lg text-gray-300">
            The newest contributions from our community.
          </p>
        </div>
        <div v-else>
          <h2 class="text-4xl sm:text-5xl font-semibold text-white">
            Welcome back, @{{ username }}
          </h2>
          <p class="mt-2 text-lg text-gray-300">
            All contributions from our community
          </p>
        </div>
      </div>

      <div>
        <hr class="my-12 h-0.5 border-t-0 bg-neutral-100 dark:bg-white/10" />
      </div>
      <div class="mx-auto mt-16 max-w-7xl px-6 lg:px-8">
        <div class="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
          <article
              v-for="post in posts"
              :key="post.postId"
              class="group relative flex flex-col overflow-hidden rounded-3xl bg-[#0f172a] border border-gray-800/50 shadow-xl transition-shadow duration-300 hover:shadow-2xl hover:shadow-indigo-500/5"
          >
            <div class="flex flex-1 flex-col justify-between p-8">
              <!-- Title & content (clickable) -->
              <div class="flex-1">
                <div class="flex items-center gap-3 text-xs">
                  <time class="text-gray-400">{{ formatDate(post.createdAt) }}</time>
                  <span class="rounded-full bg-indigo-900/30 px-3 py-1 text-indigo-300 font-medium">
                    Post
                  </span>
                </div>

                <div
                    class="mt-4 cursor-pointer rounded-xl px-2 -mx-2 py-3 transition-all duration-200 hover:bg-white/5"
                    @click="navigateToPost(post.postId)"
                >
                  <h3 class="text-xl font-bold text-white leading-tight line-clamp-2 transition-colors group-hover:text-indigo-300">
                    {{ post.title }}
                  </h3>
                  <p class="mt-4 text-sm leading-relaxed text-gray-400 line-clamp-3">
                    {{ post.content }}
                  </p>
                </div>
              </div>

              <!-- Footer -->
              <div class="mt-8 flex items-center justify-between">
                <!-- Username -->
                <button
                    @click="navigateToUser(post.creatorId)"
                    :disabled="!isLoggedIn"
                    class="flex items-center gap-3 transition-all hover:gap-4 disabled:opacity-50 disabled:cursor-not-allowed"
                    :title="isLoggedIn ? `View @${post.creatorUsername}'s profile` : 'Log in to view profile'"
                >
                  <div class="h-10 w-10 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold text-sm shadow-lg">
                    {{ post.creatorUsername[0].toUpperCase() }}
                  </div>
                  <span class="text-sm font-medium text-gray-300">@{{ post.creatorUsername }}</span>
                </button>

                <!-- Like Button — PERFECT -->
                <button
                    @click="toggleLike(post)"
                    :disabled="!isLoggedIn"
                    class="flex items-center gap-2 rounded-lg px-3 py-2 transition-all duration-200 hover:scale-105 disabled:cursor-not-allowed"
                    :class="[
                    isLoggedIn
                      ? post.likedByCurrentUser
                        ? 'bg-red-500/20 text-red-400'
                        : 'text-gray-400 hover:text-red-400 hover:bg-red-500/10'
                      : 'text-gray-600'
                  ]"
                >
                  <svg
                      class="w-5 h-5 transition-all duration-300"
                      :class="{ 'scale-125': post.likedByCurrentUser }"
                      :fill="post.likedByCurrentUser ? 'currentColor' : 'none'"
                      stroke="currentColor"
                      viewBox="0 0 20 20"
                  >
                    <path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd" />
                  </svg>
                  <span class="font-medium min-w-[2ch] text-center">
                    {{ post.likesCount || 0 }}
                  </span>
                </button>
              </div>
            </div>
          </article>
        </div>
      </div>
    </div>
  </div>
</template>