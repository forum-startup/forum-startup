<script setup>
import {watch} from 'vue'
import {useCreatePost} from '../composables/useCreatePost.js'

const {form, isLoading, error, createPost} = useCreatePost()

const handleSubmit = () => {
  createPost()
}

watch(error, (msg) => {
  if (msg) {
    alert(msg)
  }
})
</script>

<template>
  <div class="h-screen bg-gray-900 p-8">
    <div class="mx-auto w-full max-w-xl">
      <form @submit.prevent="handleSubmit" class="relative">
        <div
            class="overflow-hidden rounded-lg border border-gray-700 shadow-sm focus-within:border-indigo-500 focus-within:ring-1 focus-within:ring-indigo-500 transition-colors">

          <!-- Title -->
          <input
              v-model="form.title"
              type="text"
              id="title"
              class="block w-full border-0 bg-transparent pt-6 pb-3 px-4 text-2xl font-semibold text-white placeholder:text-gray-500 focus:ring-0 outline-none"
              placeholder="Title"
              required
          >

          <!-- Content -->
          <textarea
              v-model="form.content"
              rows="3"
              id="content"
              class="block w-full resize-none border-0 bg-transparent px-4 pb-6 text-gray-300 placeholder:text-gray-500 focus:ring-0 outline-none sm:text-lg"
              placeholder="Write a description..."
          ></textarea>

          <!-- Spacer for toolbar -->
          <div aria-hidden="true" class="py-2">
            <div class="h-9"></div>
          </div>
        </div>

        <!-- Bottom Toolbar -->
        <div class="absolute inset-x-0 bottom-0">
          <div class="flex items-center justify-between border-t border-gray-700 px-3 py-3">
            <!-- Submit Button -->
            <button
                type="submit"
                :disabled="isLoading || !form.title.trim() || !form.content.trim()"
                class="rounded-md bg-indigo-600 px-5 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            >
              {{ isLoading ? 'Creating...' : 'Create Post' }}
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>
