import {ref} from "vue";
import api from "../utils/axios.js";
import {currentUser} from "../utils/store.js";

export function usePosts() {
    const posts = ref([])
    const post = ref()
    const isLoading = ref(false)
    const error = ref(null)

    async function fetchPostsByUserId(id) {
        if (!id) return

        isLoading.value = true
        error.value = null
        try {
            const res = await api.get(`/public/posts/by-author/${id}`)
            posts.value = res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to load posts'
        } finally {
            isLoading.value = false
        }
    }

    async function fetchCurrentUserPosts() {
        if (!currentUser.value) {
            error.value = 'You must be logged in'
            return
        }

        isLoading.value = true
        error.value = null

        try {
            const res = await api.get(`/public/posts/by-author/${currentUser.value.id}`)
            posts.value = res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to load your posts'
        } finally {
            isLoading.value = false
        }
    }

    async function fetchPostById(id) {
        isLoading.value = true
        error.value = null

        try {
            const res = await api.get(`/public/posts/${id}`)
            post.value = res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to load post'
        } finally {
            isLoading.value = false
        }
    }

    async function editPostById(id) {
        isLoading.value = true
        error.value = null

        try {
            const res = await api.put(`/private/posts/${id}`)
            post.value = res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to edit post'
        } finally {
            isLoading.value = false
        }
    }

    async function deletePostById(id) {
        isLoading.value = true
        error.value = null

        try {
            const res = await api.delete(`/private/posts/${id}`)
            post.value = res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete post'
        } finally {
            isLoading.value = false
        }
    }

    async function adminDeletePostById(id) {
        isLoading.value = true
        error.value = null

        try {
            const res = await api.delete(`/admin/posts/${id}`)
            post.value = res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete post'
        } finally {
            isLoading.value = false
        }
    }

    return {
        posts,
        post,
        isLoading,
        error,
        fetchCurrentUserPosts,
        fetchPostsByUserId,
        fetchPostById,
        editPostById,
        deletePostById,
        adminDeletePostById,
    }

}