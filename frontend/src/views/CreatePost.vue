<script setup>
import { useCreatePost } from '../composables/useCreatePost.js'

const {
  form,
  errors,
  serverError,
  isLoading,
  createPost
} = useCreatePost()
</script>

<template>
  <div class="flex-1 flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-2xl">

      <div class="text-center mb-10">
        <h1 class="text-4xl font-bold tracking-tight text-white">Share your story</h1>
        <p class="mt-3 text-lg text-gray-400">Write something worth reading</p>
      </div>

      <div class="rounded-2xl bg-gray-800/50 backdrop-blur-sm ring-1 ring-white/10 shadow-2xl overflow-hidden">
        <form @submit.prevent="createPost" class="p-8 space-y-10">

          <!-- Title -->
          <div>
            <div class="flex justify-between items-center mb-3">
              <label for="title" class="text-sm font-medium text-gray-300">Title</label>
            </div>
            <input
                v-model="form.title"
                type="text"
                id="title"
                placeholder="Your title..."
                class="w-full rounded-xl border-0 bg-white/10 px-5 py-4 text-xl font-medium text-white
                     placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500 focus:outline-none
                     ring-1 ring-inset transition"
                :class="{ 'ring-red-500/50 focus:ring-red-500': errors.title }"
            />
            <p v-if="errors.title" class="mt-2 text-sm text-red-400 font-medium">{{ errors.title }}</p>
          </div>

          <!-- Content -->
          <div>
            <div class="flex justify-between items-center mb-3">
              <label for="content" class="text-sm font-medium text-gray-300">Your story</label>
            </div>
            <textarea
                v-model="form.content"
                id="content"
                rows="12"
                placeholder="Start writing your post..."
                class="w-full rounded-xl border-0 bg-white/10 px-5 py-4 text-lg text-gray-200
                     placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500 focus:outline-none
                     resize-none transition leading-relaxed ring-1 ring-inset"
                :class="{ 'ring-red-500/50 focus:ring-red-500': errors.content }"
            ></textarea>
            <p v-if="errors.content" class="mt-2 text-sm text-red-400 font-medium">{{ errors.content }}</p>
          </div>

          <!-- Submit -->
          <div class="flex flex-col items-start gap-4 pt-8 border-t border-white/10">
            <button
                type="submit"
                :disabled="isLoading || Object.keys(errors).length > 0"
                class="rounded-xl bg-indigo-600 px-8 py-4 text-lg font-semibold text-white shadow-lg
                     hover:bg-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            >
              {{ isLoading ? 'Publishing...' : 'Publish Post' }}
            </button>

            <p v-if="serverError" class="text-sm font-medium text-red-400 animate-pulse">
              {{ serverError }}
            </p>
          </div>
        </form>
      </div>

      <p class="mt-8 text-center text-sm text-gray-500">
        Title must be 16–64 characters and content must be 32–8192 characters
      </p>
    </div>
  </div>
</template>