import { ref } from 'vue'
import api from '../utils/axios.js'
import {usePostValidation} from "./usePostValidation.js";
import {usePosts} from "./usePosts.js";

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
            const res = await api.get(`/public/posts/${id}`)
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

    return {
        post,
        isLoading,
        error,
        errors,           // client-side validation errors
        serverError: error,
        fetchPostById,
        updatePost,
        deletePostById,
        adminDeletePostById,
    }
}