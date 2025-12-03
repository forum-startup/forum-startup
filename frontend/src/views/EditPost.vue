<script setup lang="ts">
import {computed, onMounted, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import {usePost} from "../composables/usePost";

const route = useRoute()
const router = useRouter()

// Get postId from URL
const postId = computed(() => route.params.postId as string)

const {
  post,
  isLoading,
  errors,
  serverError,
  fetchPostById,
  updatePost,
} = usePost()

// Fetch post when component mounts or postId changes
onMounted(() => {
  if (postId.value) {
    fetchPostById(postId.value)
  }
})

async function onSubmit() {
  const success = await updatePost()

  if (!success) {
    return
  }

  await router.push('/my-posts')
}

// re-fetch if navigating directly to /my-posts/123/edit
watch(postId, (newId) => {
  if (newId) fetchPostById(newId)
})
</script>

<template>
  <div class="flex-1 flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-2xl">

      <!-- Loading -->
      <div v-if="isLoading" class="text-center py-20">
        <p class="text-gray-400">Loading your post...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="text-center py-20">
        <p class="text-red-400 font-medium">{{ serverError }}</p>
        <button @click="fetchPostById(postId)" class="mt-4 text-indigo-400 hover:text-indigo-300">
          Try again
        </button>
      </div>

      <!-- Form — only show when post exists -->
      <div v-else-if="post"
           class="rounded-2xl bg-gray-800/50 backdrop-blur-sm ring-1 ring-white/10 shadow-2xl overflow-hidden">
        <form @submit.prevent="onSubmit" class="p-8 space-y-10">
          <!-- Title -->
          <div>
            <label class="text-sm font-medium text-gray-300">Title</label>
            <input
                v-model="post.title"
                type="text"
                required
                class="w-full rounded-xl border-0 bg-white/10 px-5 py-4 text-lg text-gray-200
                     placeholder:text-gray-500 ring-gray-600 focus:ring-2 focus:ring-indigo-500 focus:outline-none
                     resize-none transition leading-relaxed ring-1 ring-inset"
            />
            <p v-if="errors.title" class="text-red-400 text-sm mt-1">
              {{ errors.title }}
            </p>
          </div>

          <!-- Content -->
          <div>
            <label class="text-sm font-medium text-gray-300">Your story</label>
            <textarea
                v-model="post.content"
                rows="14"
                required
                class="w-full rounded-xl border-0 bg-white/10 px-5 py-4 text-lg text-gray-200
                     placeholder:text-gray-500 ring-gray-600 focus:ring-2 focus:ring-indigo-500 focus:outline-none
                     resize-none transition leading-relaxed ring-1 ring-inset"
            ></textarea>
            <p v-if="errors.content" class="text-red-400 text-sm mt-1">
              {{ errors.content }}
            </p>
          </div>

          <!-- Buttons -->
          <div class="flex gap-4 pt-6 border-t border-white/10">
            <button
                type="submit"
                :disabled="isLoading"
                class="px-8 py-4 bg-indigo-600 text-white font-semibold rounded-xl hover:bg-indigo-500 disabled:opacity-50 transition"
            >
              {{ isLoading ? 'Saving...' : 'Save Changes' }}
            </button>
            <button
                type="button"
                @click="router.push('/my-posts')"
                class="px-8 py-4 bg-gray-700 text-gray-300 font-medium rounded-xl hover:bg-gray-600 transition"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>

      <!-- Not found -->
      <div v-else class="text-center py-20 text-gray-400">
        Post not found.
      </div>
    </div>
  </div>
</template>