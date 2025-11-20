<script setup>
import {ref, onMounted} from "vue"
import axios from 'axios'
import NavBar from "../components/navigation/NavBar.vue";

const posts = ref([])

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/public/posts/recent?limit=10')
    posts.value = await response.data
  } catch (e) {
    console.error("Failed to load posts", e)
  }
})

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric"
  })
}
</script>

<template>
  <div>
    <NavBar/>
    <div class="bg-gray-900 py-24 sm:py-32">
      <div class="mx-auto max-w-7xl px-6 lg:px-8">

        <!-- Header -->
        <div class="mx-auto max-w-2xl lg:mx-0">
          <h2 class="text-4xl font-semibold tracking-tight text-pretty text-white sm:text-5xl">Latest Posts</h2>
          <p class="mt-2 text-lg/8 text-gray-300">The newest contributions from our community.</p>
        </div>

        <!-- Posts -->
        <div
            class="mx-auto mt-10 grid max-w-2xl grid-cols-1 gap-x-8 gap-y-16 border-t border-gray-700 pt-10 sm:mt-16 sm:pt-16 lg:mx-0 lg:max-w-none lg:grid-cols-3">
          <article
              v-for="post in posts"
              :key="post.id"
              class="flex max-w-xl flex-col items-start justify-between">

            <!-- Date -->
            <div class="flex items-center gap-x-4 text-xs">
              <time class="text-gray-400">
                {{ formatDate(post.createdAt) }}
              </time>

              <span
                  class="rounded-full bg-gray-800/60 px-3 py-1.5 font-medium text-gray-300">
              Post
            </span>
            </div>

            <!-- Title & Summary -->
            <div class="group relative grow">
              <h3 class="mt-3 text-lg font-semibold text-white group-hover:text-gray-300">
                <a :href="`/post/${post.postId}`">
                  <span class="absolute inset-0"></span>
                  {{ post.title }}
                </a>
              </h3>
              <p class="mt-5 line-clamp-3 text-sm text-gray-400">
                {{ post.content }}
              </p>
            </div>

            <!-- Author -->
            <div class="relative mt-8 flex items-center gap-x-4 justify-self-end">
              <!--            <img-->
              <!--                :src="post.user.profilePhotoUrl || 'https://via.placeholder.com/80'"-->
              <!--                alt=""-->
              <!--                class="size-10 rounded-full bg-gray-800"/>-->

              <div class="text-sm">
                <!--              <p class="font-semibold text-white">-->
                <!--                {{ post.user.firstName }} {{ post.user.lastName }}-->
                <!--              </p>-->
                <p class="text-gray-400">@{{ post.creatorUsername }}</p>
              </div>
            </div>
          </article>
        </div>

      </div>
    </div>
  </div>
</template>