<script setup>
import {ref, onMounted} from "vue"
import axios from 'axios'
import NavBar from "../components/navigation/NavBar.vue";
import Footer from "../components/Footer.vue";

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
  <div class="bg-gray-900 min-h-screen flex flex-col">
    <NavBar/>

    <div class="home-section">
      <div class="page-container">

        <!-- Header -->
        <div class="mx-auto max-w-2xl lg:mx-0">
          <h2 class="latest-posts-title">Latest Posts</h2>
          <p class="latest-posts-subtitle">The newest contributions from our community.</p>
        </div>

        <!-- Posts -->
        <div class="posts-grid">
          <article
              v-for="post in posts"
              :key="post.id"
              class="post-card">

            <div class="post-card-top">
              <!-- Date -->
              <div class="post-date">
                <time>
                  {{ formatDate(post.createdAt) }}
                </time>

                <span class="post-tag">
                Post
              </span>
              </div>

              <!-- Title & Summary -->
              <div class="group relative grow">
                <h3 class="post-title">
                  <a :href="`/post/${post.postId}`">
                    <span class="absolute inset-0"></span>
                    {{ post.title }}
                  </a>
                </h3>
                <p class="post-summary">
                  {{ post.content }}
                </p>
              </div>
            </div>

            <!-- Author -->
            <div class="post-card-bottom">
              <div class="post-author">
                  <p>@{{ post.creatorUsername }}</p>
              </div>
              <div class="post-likes">
                  <p>Likes {{ post.likesCount }}</p>
              </div>
            </div>

          </article>
        </div>

      </div>
    </div>
    <Footer />
  </div>
</template>

<style>
/* Page background */
.home-section {
  background-color: #111827; /* bg-gray-900 */
  padding-top: 6rem; /* py-24 */
  padding-bottom: 8rem; /* sm:py-32 */
  height: 100%;
}

/* Page container */
.page-container {
  max-width: 80rem; /* max-w-7xl */
  margin-left: auto;
  margin-right: auto;
  padding-left: 1.5rem; /* px-6 */
  padding-right: 1.5rem;
}

@media (min-width: 1024px) {
  .page-container {
    padding-left: 2rem; /* lg:px-8 */
    padding-right: 2rem;
  }
}

/* Header text */
.latest-posts-title {
  font-size: 2.25rem; /* text-4xl */
  font-weight: 600; /* font-semibold */
  color: white;
}

@media (min-width: 640px) {
  .latest-posts-title {
    font-size: 3rem; /* sm:text-5xl */
  }
}

.latest-posts-subtitle {
  margin-top: 0.5rem;
  font-size: 1.125rem;
  line-height: 2rem;
  color: #d1d5db; /* text-gray-300 */
}

/* Posts grid */
.posts-grid {
  margin-left: auto;
  margin-right: auto;
  margin-top: 2.5rem; /* mt-10 */
  padding-top: 2.5rem; /* pt-10 */
  max-width: 42rem; /* max-w-2xl */
  display: grid;
  grid-template-columns: 1fr;
  gap: 4rem 2rem; /* gap-x-8 gap-y-16 */
  border-top: 1px solid #374151; /* border-gray-700 */
}

@media (min-width: 640px) {
  .posts-grid {
    margin-top: 4rem; /* sm:mt-16 */
    padding-top: 4rem; /* sm:pt-16 */
  }
}

@media (min-width: 1024px) {
  .posts-grid {
    max-width: none;
    grid-template-columns: repeat(3, 1fr);
  }
}

/* Post card */
.post-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  max-width: 24rem;
  background-color: #1b263b;
  border-radius: 1.7rem;
  padding: 1rem 1.5rem;
  gap: 2rem;
  box-shadow: 0px 0px 15px #0d1b2a;
  transition: 0.1s ease-in-out;
}

/* Post card hover */
.post-card:hover {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  max-width: 24rem;
  background-color: #253450;
  border-radius: 1.7rem;
  padding: 1rem 1.5rem;
  gap: 2rem;
  box-shadow: 0px 0px 15px #0d1b2a;
  cursor: pointer;
}

.post-card-top {

}

.post-date {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.75rem; /* text-xs */
  color: #9ca3af; /* text-gray-400 */
}

/* Pill */
.post-tag {
  background-color: rgba(31, 41, 55, 0.6); /* bg-gray-800/60 */
  padding: 0.375rem 0.75rem; /* px-3 py-1.5 */
  border-radius: 9999px;
  font-weight: 500;
  color: #d1d5db;
}

/* Title */
.post-title {
  margin-top: 0.75rem;
  font-size: 1.125rem; /* text-lg */
  font-weight: 600;
  color: white;
  position: relative;
}

.post-title:hover {
  color: #d1d5db; /* hover:text-gray-300 */
}

/* Summary */
.post-summary {
  margin-top: 1.25rem;
  font-size: 0.875rem;
  color: #9ca3af;
  display: -webkit-box;
  -webkit-line-clamp: 3; /* line-clamp-3 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Author and Likes container */
.post-card-bottom {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  font-size: 1rem; /* text-lg */
  font-weight: 600;
  color: white;
}

/* Author */
.post-author {

}

/* Likes */
.post-likes {

}
</style>