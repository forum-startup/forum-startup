<script setup>
import { useComment } from '../composables/useComment'
import {watchEffect} from "vue";

const props = defineProps({
  postId: [String, Number]
})

const emit = defineEmits(['commented'])

const { form, isLoading, errors, error, createComment, startReply } = useComment()

// Auto-set postId
watchEffect(() => {
  form.value.postId = props.postId
})

async function submit() {
  const success = await createComment()
  if (success) {
    form.value.content = ''
    emit('commented')
  }
}
</script>

<template>
  <div>
    <textarea
        v-model="form.content"
        rows="4"
        placeholder="Share your thoughts..."
        class="w-full rounded-xl bg-white/10 px-5 py-4 text-white placeholder-gray-500 focus:ring-2 focus:ring-indigo-500 focus:outline-none resize-none"
    ></textarea>
    <p v-if="errors.content" class="text-red-400 text-sm mt-2">{{ errors.content }}</p>
    <p v-if="error" class="text-red-400 text-sm mt-2">{{ error }}</p>

    <button
        @click="submit"
        :disabled="isLoading || !form.content.trim()"
        class="mt-4 px-6 py-3 bg-indigo-600 text-white rounded-xl hover:bg-indigo-500 disabled:opacity-50 font-medium"
    >
      {{ isLoading ? 'Posting...' : 'Post Comment' }}
    </button>
  </div>
</template>