<script setup>
import { onMounted } from "vue";
import { usePosts } from "../composables/usePosts.js";
import { usePost } from "../composables/usePost.js";
import { useRouter } from "vue-router";
import { currentUser } from "../utils/store.js"; // your auth store

const { posts, fetchRecentPosts } = usePosts();
const { toggleLike } = usePost();
const router = useRouter();

onMounted(fetchRecentPosts);

function navigateToPost(postId) {
  router.push({ name: "Post", params: { postId } });
}

function navigateToUser(userId) {
  if (!currentUser.value) return; // prevent navigation if not logged in
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
      <!-- Header -->
      <div class="mx-auto max-w-2xl lg:mx-0">
        <h2 class="text-4xl sm:text-5xl font-semibold text-white">Latest Posts</h2>
        <p class="mt-2 text-lg text-gray-300">
          The newest contributions from our community.
        </p>
      </div>

      <!-- Posts Grid -->
      <div class="mx-auto mt-16 max-w-7xl px-6 lg:px-8">
        <div class="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
          <article
              v-for="post in posts"
              :key="post.postId"
              class="group relative flex flex-col overflow-hidden rounded-3xl bg-[#0f172a] border border-gray-800/50 shadow-xl transition-shadow duration-300 hover:shadow-2xl hover:shadow-indigo-500/5"
          >
            <div class="flex flex-1 flex-col justify-between p-8">
              <div class="flex-1">
                <!-- Date + Badge -->
                <div class="flex items-center gap-3 text-xs">
                  <time class="text-gray-400">{{ formatDate(post.createdAt) }}</time>
                  <span
                      class="rounded-full bg-indigo-900/30 px-3 py-1 text-indigo-300 font-medium"
                  >
                    Post
                  </span>
                </div>

                <!-- Clickable Title & Content Area -->
                <div
                    class="mt-4 cursor-pointer rounded-xl px-2 -mx-2 py-3 transition-all duration-200 hover:bg-white/5"
                    @click="navigateToPost(post.postId)"
                >
                  <h3
                      class="text-xl font-bold text-white leading-tight line-clamp-2 transition-colors group-hover:text-indigo-300"
                  >
                    {{ post.title }}
                  </h3>
                  <p class="mt-4 text-sm leading-relaxed text-gray-400 line-clamp-3">
                    {{ post.content }}
                  </p>
                </div>
              </div>

              <!-- Footer: Author + Like -->
              <div class="mt-8 flex items-center justify-between">
                <!-- Username (clickable only if logged in) -->
                <button
                    @click="navigateToUser(post.creatorId)"
                    :class="{ 'cursor-not-allowed opacity-50': !currentUser?.value }"
                    class="flex items-center gap-3 transition-all hover:gap-4"
                    :title="currentUser?.value ? `View @${post.creatorUsername}'s profile` : 'Log in to view profile'"
                >
                  <div
                      class="h-10 w-10 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold text-sm shadow-lg"
                  >
                    {{ post.creatorUsername[0].toUpperCase() }}
                  </div>
                  <span class="text-sm font-medium text-gray-300">
                    @{{ post.creatorUsername }}
                  </span>
                </button>

                <!-- Like Button (only active if logged in) -->
                <button
                    @click="toggleLike(post)"
                    class="flex items-center gap-2 rounded-lg px-3 py-2 transition-all duration-200"
                    :class="
                    currentUser?.value
                      ? post.isLiked
                        ? 'bg-red-500/10 text-red-400 hover:bg-red-500/20'
                        : 'text-gray-400 hover:text-red-400 hover:bg-red-500/10'
                      : 'text-gray-600 cursor-not-allowed'
                  "
                    :title="currentUser?.value ? (post.isLiked ? 'Unlike' : 'Like') : 'Log in to like'"
                >
                  <svg
                      class="w-5 h-5 transition-transform"
                      :class="{ 'scale-110': post.isLiked }"
                      :fill="post.isLiked ? 'currentColor' : 'none'"
                      stroke="currentColor"
                      viewBox="0 0 20 20"
                  >
                    <path
                        fill-rule="evenodd"
                        d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z"
                        clip-rule="evenodd"
                    />
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