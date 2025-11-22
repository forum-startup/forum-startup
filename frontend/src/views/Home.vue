<script setup>
import { ref, onMounted } from "vue"
import axios from "axios"

const posts = ref([])

onMounted(async () => {
  try {
    const response = await axios.get(
        "http://localhost:8080/api/public/posts/recent?limit=10"
    )
    posts.value = response.data
  } catch (e) {
    console.error("Failed to load posts", e)
  }
})

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  })
}
</script>

<template>
  <div class="bg-gray-900 min-h-screen flex flex-col">

    <!-- MAIN SECTION -->
    <div class="bg-gray-900 pt-24 pb-32 flex-1">
      <div class="mx-auto max-w-7xl px-6 lg:px-8">

        <!-- Header -->
        <div class="mx-auto max-w-2xl lg:mx-0">
          <h2 class="text-4xl sm:text-5xl font-semibold text-white">
            Latest Posts
          </h2>
          <p class="mt-2 text-lg text-gray-300">
            The newest contributions from our community.
          </p>
        </div>

        <!-- Posts Grid -->
        <div
            class="mx-auto mt-10 max-w-2xl border-t border-gray-700
          grid grid-cols-1 gap-y-16 gap-x-8 pt-10
          sm:mt-16 sm:pt-16
          lg:max-w-none lg:grid-cols-3">

          <article
              v-for="post in posts"
              :key="post.id"
              class="flex flex-col justify-between max-w-xl
            bg-[#1b263b] rounded-3xl px-6 py-6 gap-8
            shadow-[0_0_15px_#0d1b2a]
            transition duration-90
            hover:bg-[#253450] hover:shadow-[0_0_20px_#0d1b2a] hover:cursor-pointer">

            <!-- Date + Tag -->
            <div class="flex flex-col gap-4">
              <div class="flex items-center gap-x-4 text-xs text-gray-400">
                <time>{{ formatDate(post.createdAt) }}</time>

                <span
                    class="rounded-full bg-gray-800/60 px-3 py-1.5 font-medium text-gray-300">
                  Post
                </span>
              </div>

              <!-- Title & Summary -->
              <div class="group relative grow">
                <h3
                    class="mt-3 text-lg font-semibold text-white group-hover:text-gray-300 transition">
                  <a :href="`/post/${post.postId}`">
                    <span class="absolute inset-0"></span>
                    {{ post.title }}
                  </a>
                </h3>

                <p class="mt-5 text-sm text-gray-400 line-clamp-3">
                  {{ post.content }}
                </p>
              </div>
            </div>

            <!-- Author + Likes -->
            <div class="flex justify-between text-white font-semibold">
              <p>@{{ post.creatorUsername }}</p>
              <p>Likes {{ post.likesCount }}</p>
            </div>
          </article>

        </div>
      </div>
    </div>
  </div>
</template>