import { ref } from 'vue'
import api from '../utils/axios.js'
import {usePostValidation} from "./usePostValidation.js";
import {currentUser} from "../utils/store.js";

export function usePost() {
    const post = ref(null)
    const isLoading = ref(false)
    const error = ref(null)


    const { errors, validate, clearErrors } = usePostValidation(post)

    async function fetchPostById(id) {
        if (!id) return

        isLoading.value = true
        error.value = null

        try {
            const res = await api.get(`/private/posts/${id}`)
            post.value = res.data
            clearErrors()
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to load post'
        } finally {
            isLoading.value = false
        }
    }

    async function updatePost() {
        clearErrors()

        if (!validate()) {
            return false
        }

        if (!post.value?.postId) return false

        isLoading.value = true
        error.value = null

        try {
            await api.put(`/private/posts/${post.value.postId}`, {
                title: post.value.title.trim(),
                content: post.value.content.trim(),
            })
            return true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to update post'
            return false
        } finally {
            isLoading.value = false
        }
    }

    async function likePost(postId) {
        clearErrors()

        if (!post.value?.postId) return false

        isLoading.value = true
        error.value = null

        try {
            await api.post(`/private/posts/${postId}/like`)
            return true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to like post'
            return false
        } finally {
            isLoading.value = false
        }
    }

    async function unlikePost(postId) {
        clearErrors()

        if (!post.value?.postId) return false

        isLoading.value = true
        error.value = null

        try {
            await api.post(`/private/posts/${postId}/unlike`)
            return true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to unlike post'
            return false
        } finally {
            isLoading.value = false
        }
    }


    async function deletePostById(id) {
        isLoading.value = true
        error.value = null

        try {
            await api.delete(`/private/posts/${id}`)
            post.value = null

            return true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete post'
            return false
        } finally {
            isLoading.value = false
        }
    }

    async function adminDeletePostById(id) {
        isLoading.value = true
        error.value = null

        try {
            await api.delete(`/admin/posts/${id}`)
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete post'
        } finally {
            isLoading.value = false
        }
    }

    async function toggleLike(post) {
        if (!currentUser.value) return

        const wasLiked = post.isLiked

        // Optimistic update
        post.isLiked = !wasLiked
        post.likesCount += wasLiked ? -1 : 1

        try {
            if (wasLiked) {
                await unlikePost(post.postId)
            } else {
                await likePost(post.postId)
            }
        } catch (error) {
            // Revert on failure
            post.isLiked = wasLiked
            post.likesCount += wasLiked ? 1 : -1
            console.error("Like failed:", error)
        }
    }

    return {
        post,
        isLoading,
        error,
        errors,           // client-side validation errors
        serverError: error,
        fetchPostById,
        updatePost,
        likePost,
        unlikePost,
        deletePostById,
        adminDeletePostById,
        toggleLike
    }
}