package com.example.playlistmaker.library.ui.playlists

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.utils.convertDpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding: FragmentNewPlaylistBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private var imageUri: Uri? = null

    private val viewModel by viewModel<NewPlaylistViewModel>()

    private val args by navArgs<NewPlaylistFragmentArgs>()

    val dialog by lazy {
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Center)
            .setTitle(requireActivity().getString(R.string.new_playlist_dialog_title))
            .setMessage(requireActivity().getString(R.string.new_playlist_dialog_message))
            .setNegativeButton(
                requireActivity().getString(R.string.new_playlist_dialog_negative)
            ) { dialog, which ->
            }
            .setPositiveButton(
                requireActivity().getString(R.string.new_playlist_dialog_positive)
            ) { dialog, which ->
                findNavController().navigateUp()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val argPlaylist = args.playlist

        if (argPlaylist != null) {
            binding.etName.setText(argPlaylist.playlistName)
            binding.etDescription.setText(argPlaylist.playlistDescription)
            if (argPlaylist.playlistCoverPath != "null") {
                imageUri = Uri.parse(argPlaylist.playlistCoverPath)
                setImageToView(Uri.parse(argPlaylist.playlistCoverPath))
            }
            binding.toolbar.title = requireContext().getString(R.string.edit_playlist_title)
            binding.buttonCreate.text = requireContext().getString(R.string.button_save_text)
            binding.buttonCreate.isEnabled = true
        } else {
            binding.toolbar.title = requireContext().getString(R.string.new_playlist_title)
            binding.buttonCreate.text = requireContext().getString(R.string.button_create_text)

        }


        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    imageUri = uri
                    setImageToView(uri)
                }
            }

        binding.ivPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.buttonCreate.setOnClickListener {
            val uri = imageUri
            if (uri != null && imageUri != argPlaylist?.playlistCoverPath?.toUri()) {
                imageUri = saveImageToPrivateStorage(uri)
            }
            val playlistId = args.playlist?.playlistId ?: 0
            val tracksCount = args.playlist?.tracksCount ?: 0

            val playlist = Playlist(
                playlistId,
                binding.etName.text.toString(),
                binding.etDescription.text.toString(),
                imageUri.toString(),
                emptyList(),
                tracksCount
            )

            viewModel.addPlaylist(playlist)
            findNavController().navigateUp()
        }
        binding.etName.addTextChangedListener(
            onTextChanged = { charSequence, _, _, _ ->
                binding.buttonCreate.isEnabled = !charSequence.isNullOrBlank()
            }
        )
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onBackPressed() {
        if (args.playlist == null) {
            if (
                imageUri != null
                || binding.etName.text.toString().isNotBlank()
                || binding.etDescription.text.toString().isNotBlank()
            ) {
                dialog.show()
            } else {
                findNavController().navigateUp()
            }
        } else {
            findNavController().navigateUp()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): Uri {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlistsImages"
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, uri.toString().substringAfterLast("/"))

        val inputStream = requireActivity().contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.toUri()
    }

    private fun setImageToView(uri: Uri) {
        binding.ivPhoto.scaleType = ImageView.ScaleType.CENTER_CROP

        Glide.with(this)
            .load(uri)
            .apply(
                RequestOptions().transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(
                            requireContext().convertDpToPx(
                                COVER_CORNER_RADIUS_IN_DP
                            )
                        )
                    )
                )
            )
            .into(binding.ivPhoto)
    }

    private companion object {
        const val COVER_CORNER_RADIUS_IN_DP = 8f
    }
}