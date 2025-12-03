<script setup>
import {computed, onMounted, ref, watch} from "vue";
import {useRoute} from "vue-router";
import {useComments} from "../composables/useComments";
import {usePost} from "../composables/usePost";
import CommentItem from "../components/CommentItem.vue";
import {format} from "date-fns";
import CommentForm from "../components/CommentForm.vue";
import {useAuth} from "../composables/useAuth.js";

const route = useRoute()
const postId = computed(() => route.params.postId)
const {hasRole} = useAuth()
const {deletePostById, adminDeletePostById, isOwnPost} = usePost()
const isAdmin = hasRole('ROLE_ADMIN')
const showMenu = ref(false)

const {
  post,
  isLoading: postLoading,
  error: postError,
  fetchPostById,
  toggleLike,
} = usePost()

const {
  comments,
  isLoading: commentsLoading,
  error: commentsError,
  fetchCommentsByPostId
} = useComments()

async function loadData() {
  if (!postId.value) return
  await Promise.all([
    fetchPostById(postId.value),
    fetchCommentsByPostId(postId.value)
  ])
}

async function handleDelete() {
  if (!isAdmin) {
    await deletePostById(postId.value)
  } else {
    await adminDeletePostById(postId.value, showMenu)
  }
}

onMounted(loadData)
watch(postId, loadData)
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900/20 to-slate-900">
    <div class="mx-auto max-w-4xl px-6 py-16">

      <!-- Loading -->
      <div v-if="postLoading || commentsLoading" class="text-center py-32">
        <div
            class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-indigo-500 border-t-transparent"></div>
        <p class="mt-6 text-gray-400 text-lg">Loading your post...</p>
      </div>

      <!-- Error -->
      <div v-else-if="postError || commentsError" class="text-center py-32">
        <p class="text-red-400 text-xl font-medium">
          {{ postError || commentsError }}
        </p>
      </div>

      <!-- Post Content -->
      <article v-else-if="post" class="space-y-12">
        <!-- Header -->
        <header>
          <h1 class="text-4xl md:text-5xl font-bold text-white leading-tight">
            {{ post.title }}
          </h1>

          <div class="flex flex-wrap items-center gap-6 mt-8 text-gray-300">
            <div class="flex items-center gap-4">
              <div
                  class="w-14 h-14 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white text-xl font-bold shadow-lg">
                {{ post.creatorUsername[0].toUpperCase() }}
              </div>
              <div>
                <p class="font-semibold text-white">@{{ post.creatorUsername }}</p>
                <time class="text-sm text-gray-400">
                  {{ format(new Date(post.createdAt), 'MMMM d, yyyy • h:mm a') }}
                </time>
              </div>
            </div>

            <div class="flex items-center gap-2  ml-auto">
              <!-- Like Button -->
              <button
                  @click="toggleLike(post)"
                  class="flex items-center gap-2 rounded-lg px-3 py-2 transition-all duration-200 hover:scale-105 disabled:cursor-not-allowed"
                  :class="post.likedByCurrentUser
                  ? 'bg-red-500/20 text-red-400'
                  : 'text-gray-400 hover:text-red-400 hover:bg-red-500/10'"
              >
                <svg
                    class="w-5 h-5 transition-all duration-300"
                    :class="{ 'scale-125': post.likedByCurrentUser }"
                    :fill="post.likedByCurrentUser ? 'currentColor' : 'none'"
                    stroke="currentColor"
                    viewBox="0 0 20 20"
                >
                  <path fill-rule="evenodd"
                        d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z"
                        clip-rule="evenodd"/>
                </svg>
                <span class="font-medium min-w-[2ch] text-center">
                    {{ post.likesCount || 0 }}
                  </span>
              </button>

              <div v-if="isAdmin || isOwnPost(post)" class="relative">
                <button @click.stop="showMenu = !showMenu" class="p-2 rounded-lg hover:bg-white/10 transition">
                  <svg class="w-5 h-5 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                    <path
                        d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z"/>
                  </svg>
                </button>

                <div v-if="showMenu" @click.stop
                     class="absolute right-0 mt-2 w-48 bg-gray-800 rounded-lg shadow-2xl border border-gray-700 py-2 z-50">
                  <router-link
                      :to="{ name: 'EditPost', params: { postId: post.postId } }"
                      class="block w-full text-left px-5 py-3 text-sm text-blue-400 hover:bg-blue-900/20">
                    Edit
                  </router-link>
                  <button
                      @click="handleDelete().then(success => success && (showMenu = false) && $emit('post-deleted', post.id))"
                      class="w-full text-left px-5 py-3 text-sm text-red-400 hover:bg-red-900/20">
                    Delete
                  </button>
                </div>

              </div>
            </div>
          </div>
        </header>

        <div>
          <hr class="my-12 h-0.5 border-t-0 bg-neutral-100 dark:bg-white/10"/>
        </div>

        <!-- Content -->
        <div class="bg-white/5 backdrop-blur-xl rounded-3xl p-10 border border-white/10 shadow-2xl">
          <div class="prose prose-invert prose-lg max-w-none">
            <p class="whitespace-pre-wrap text-gray-100 leading-relaxed text-lg">
              {{ post.content }}
            </p>
          </div>
        </div>

        <!-- Tags -->
        <div v-if="post.tags && post.tags.length" class="flex flex-wrap gap-3">
          <span
              v-for="tag in post.tags"
              :key="tag"
              class="px-5 py-2 rounded-full bg-gradient-to-r from-indigo-900/50 to-purple-900/50 text-indigo-300 font-medium border border-indigo-500/30 text-sm"
          >
            #{{ tag }}
          </span>
        </div>
      </article>

      <div>
        <hr class="my-12 h-0.5 border-t-0 bg-neutral-100 dark:bg-white/10"/>
      </div>

      <!-- Comments Section -->
      <section class="mt-20">
        <div class="mb-12">
          <h3 class="text-xl font-bold text-white mb-6">Leave a comment</h3>
          <CommentForm :post-id="postId" @commented="loadData"/>
        </div>

        <div class="space-y-10">
          <!-- Root Comments Only -->
          <template v-for="comment in comments.filter(c => !c.parentId)" :key="comment.id">
            <CommentItem :comment="comment" :all-comments="comments" :depth="0" :post-id="postId"/>
          </template>

          <!-- No Comments -->
          <div v-if="!comments.length && !commentsLoading" class="text-center py-20">
            <p class="text-2xl text-gray-500">No comments yet.</p>
            <p class="text-gray-400 mt-2">Be the first to share your thoughts!</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>