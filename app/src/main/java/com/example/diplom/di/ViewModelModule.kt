package com.example.diplom.di

import com.example.diplom.presentation.ui.auth.signin.SignInViewModel
import com.example.diplom.presentation.ui.auth.signup.SignUpViewModel
import com.example.diplom.presentation.ui.chat.ChatViewModel
import com.example.diplom.presentation.ui.chats.ChatsViewModel
import com.example.diplom.presentation.ui.createpost.CreatePostViewModel
import com.example.diplom.presentation.ui.editpersoninfo.EditPersonInfoViewModel
import com.example.diplom.presentation.ui.home.NewsViewModel
import com.example.diplom.presentation.ui.mainfragment.MainViewModel
import com.example.diplom.presentation.ui.myprofile.MyProfileViewModel
import com.example.diplom.presentation.ui.post.PostViewModel
import com.example.diplom.presentation.ui.posts.PostsViewModel
import com.example.diplom.presentation.ui.prifile.ProfileViewModel
import com.example.diplom.presentation.ui.settings.SettingsViewModel
import com.example.diplom.presentation.ui.splash.SplashViewModel
import com.example.diplom.presentation.ui.subscribers.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SignUpViewModel(
            signUpUseCase = get(),
            getCategoriesUseCase = get(),
            isTakenEmailUseCase = get(),
            subscribeNotificationsUseCase = get()
        )
    }
    viewModel { SignInViewModel(signInUseCase =  get(), subscribeNotificationsUseCase = get()) }
    viewModel { SplashViewModel(tokenService = get()) }
    viewModel {
        MyProfileViewModel(
            getMyProfileUseCase = get(),
            getProfileUseCase = get(),
            getPostsByUserIdUseCase = get(),
            setLikeUseCase = get(),
            unsetLikeUseCase = get(),
            subscribeUseCase = get(),
            unsubscribeUseCase = get()
        )
    }
    viewModel { SettingsViewModel(tokenService = get(), unsubscribeNotificationsUseCase = get()) }
    viewModel { NewsViewModel(isMyUserIdUseCase = get())}
    viewModel { ChatsViewModel(getChatsUseCase = get()) }
    viewModel { ProfileViewModel(getMyProfileUseCase = get()) }
    viewModel {
        EditPersonInfoViewModel(
            getMyProfileUseCase = get(),
            getCategoriesUseCase = get(),
            uploadUserPhotoUseCase = get()
        )
    }
    viewModel {
        ChatViewModel(
            getChatByChatIdUseCase = get(),
            getChatByUserIdUseCase = get(),
            getMessagesUseCase = get(),
            readMessageUseCase = get(),
            webSocketClient = get(),
            getMessagesFromMessageIdUseCase = get()
        )
    }
    viewModel { PostViewModel(getCommentsUseCase = get(), getPostByIdUseCase = get(), createCommentUseCase = get())}
    viewModel { CreatePostViewModel(getCategoriesUseCase = get(), createPostUseCase = get(), uploadPhotoUseCase = get())}
    viewModel { SearchViewModel(findUsersUseCase = get(), isMyUserIdUseCase = get())}
    viewModel {
        PostsViewModel(
            getMySubscriptionsPostUseCase = get(),
            getPostsByUserIdUseCase = get(),
            getAllPostsUseCase = get(),
            setLikeUseCase = get(),
            unsetLikeUseCase = get()
        )
    }
    viewModel { MainViewModel(getUnreadDialogsCountUseCase = get())}


}