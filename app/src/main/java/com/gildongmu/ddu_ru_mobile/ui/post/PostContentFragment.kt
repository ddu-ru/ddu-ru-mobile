package com.gildongmu.ddu_ru_mobile.ui.post

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.gildongmu.ddu_ru_mobile.R
import com.gildongmu.ddu_ru_mobile.databinding.FragmentPostContentBinding
import com.gildongmu.ddu_ru_mobile.model.post.request.PostCreateRequest
import com.gildongmu.ddu_ru_mobile.network.NetworkModule
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class PostContentFragment : Fragment() {
    val REQUEST_CODE_PERMISSIONS = 1001
    private var _binding: FragmentPostContentBinding? = null
    private val binding get() = _binding!!
    private val tagsList: MutableList<String> = mutableListOf()  // 해시태그를 저장할 리스트

    // ViewModel 추가
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observer 설정
        setupObservers()

        setupHashtagFunctionality()
        setupInputValidation()  // 입력 검증 기능 추가

        binding.imageViewButton.setOnClickListener {
            getGalleryPermission()
        }

        binding.deleteButton.setOnClickListener {
            val imageView: ImageView = binding.imageViewButton
            imageView.setImageURI(null)  // 이미지를 지우기
            binding.deleteButton.visibility = View.GONE  // 삭제 버튼 숨기기

            // ViewModel에서 이미지 URI 제거
            viewModel.setPhotoUrls(arrayOf(""))
            Log.d("PostContent", "이미지 URI 제거됨")
        }
    }

    // ---------- Observer 설정 ----------
    private fun setupObservers() {
        // ViewModel의 LiveData를 observe
        viewModel.title.observe(viewLifecycleOwner, Observer { title ->
            // 제목이 변경되었을 때의 처리
            if (title.isNotEmpty() && title != binding.editTextTitle.text.toString()) {
                binding.editTextTitle.setText(title)
            }
        })

        viewModel.content.observe(viewLifecycleOwner, Observer { content ->
            // 내용이 변경되었을 때의 처리
            if (content.isNotEmpty() && content != binding.editTextContent.text.toString()) {
                binding.editTextContent.setText(content)
            }
        })

        viewModel.tags.observe(viewLifecycleOwner, Observer { tags ->
            // 해시태그가 변경되었을 때의 처리
            if (tags.isNotEmpty() && tags[0].isNotEmpty()) {
                val tagsText = tags.joinToString(" ")
                if (tagsText != binding.editTextTag.text.toString()) {
                    binding.editTextTag.setText(tagsText)
                    updateTextWithColor(tagsText)
                }
            }
        })
    }

    // 갤러리 선택 결과 처리 등록을 클래스 레벨로 이동
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val imageView: ImageView = binding.imageViewButton
            imageView.setImageURI(it)  // 이미지 선택 후 이미지 뷰에 URI 설정
            binding.deleteButton.visibility = View.VISIBLE

            // ViewModel에 이미지 URI 저장
            viewModel.setPhotoUrls(arrayOf(it.toString()))
        }
    }

    fun getGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상에서 미디어 권한을 요청합니다.
            val permissions = arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
            )

            // 권한이 이미 허용되었는지 확인
            if (permissions.all { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }) {
                // 권한이 이미 허용된 경우, 갤러리 열기
                openGallery()
            } else {
                // 권한 요청
                ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_CODE_PERMISSIONS)
            }
        } else {
            // Android 13 미만에서는 기존의 READ_EXTERNAL_STORAGE 권한을 사용합니다.
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 권한이 이미 허용된 경우, 갤러리 열기
                openGallery()
            } else {
                // 권한 요청
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }
        }
    }

    // 권한 요청 결과 처리 (Activity에서 오버라이드해야 함)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openGallery()  // 권한이 허용되면 갤러리 열기
            } else {
                // 권한이 거부된 경우, 사용자에게 알리기
                Toast.makeText(requireContext(), "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 갤러리 열기 함수
    fun openGallery() {
        getContent.launch("image/*")  // 이미지 선택을 유도
    }

    private fun setupHashtagFunctionality() {
        // Focus 되었을 때 자동으로 # 추가
        binding.editTextTag.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.editTextTag.text.isNullOrEmpty()) {
                binding.editTextTag.setText("#")
                binding.editTextTag.setSelection(binding.editTextTag.text.length)
            }
        }

        binding.editTextTag.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val currentText = s.toString()

                // 공백이 입력된 경우, 새로운 # 추가
                if (currentText.endsWith(" ") && !currentText.trim().endsWith("#")) {
                    val newText = currentText.trim() + "  #"  // 공백 뒤에 새로운 # 추가
                    binding.editTextTag.setText(newText)  // 텍스트 필드에 업데이트
                    updateTextWithColor(newText)  // 해시태그 색상 업데이트
                    binding.editTextTag.setSelection(newText.length)  // 커서를 끝으로 이동
                }

                syncTagsWithViewModel()
            }
        })

        binding.editTextTag.setOnKeyListener { view, keyCode, event ->
            //삭제를 눌렀을 때
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                val currentText = binding.editTextTag.text.toString()
                val cursorPosition = binding.editTextTag.selectionStart

                // 커서 위치가 마지막이 아닐 때
                if (cursorPosition < currentText.length - 1) {
                    // 커서 앞의 문자가 #이고 뒷 문자가 공백이 아니면
                    if (currentText[cursorPosition - 1] == '#' && currentText[cursorPosition + 1] != ' ') {
                        return@setOnKeyListener true  // true 반환하여 # 삭제를 막음
                    }
                }

                // 삭제 버튼(Backspace) 눌렀을 때 #과 공백을 동시에 삭제
                if (currentText.endsWith("  #")) {
                    val newText = if (currentText.length > 2) currentText.substring(0, currentText.length - 3) else currentText
                    binding.editTextTag.setText(newText)  // 텍스트 필드에 업데이트
                    updateTextWithColor(newText)
                    binding.editTextTag.setSelection(newText.length)  // 커서를 끝으로 이동
                    return@setOnKeyListener true  // 이벤트 처리 완료
                }
            }
            // 엔터를 소모하더라도 저장 한번 보장
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                syncTagsWithViewModel()
                return@setOnKeyListener true
            }
            false
        }

        // 입력 완료 후 해시태그 색상 변경
        binding.editTextTag.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val currentText = binding.editTextTag.text.toString().trim()
                if (currentText.isNotEmpty() && currentText.startsWith("#")) {
                    tagsList.add(currentText)
                    updateTextWithColor(currentText)  // 해시태그 색상 업데이트
                    syncTagsWithViewModel()
                }
                true
            } else {
                false
            }
        }
    }

    private fun syncTagsWithViewModel() {
        val text = binding.editTextTag.text?.toString().orEmpty()
        // 공백 기준 분할 후, #으로 시작하는 토큰만
        val tagsArray = text.trim()
            .split("#")
            .map { it.trim() }
            .toTypedArray()
        viewModel.setTags(tagsArray)
    }

    // 텍스트를 색상별로 구분하여 표시하는 함수
    private fun updateTextWithColor(text: String) {
        val spannable = SpannableString(text)
        val hashTagPattern = "#\\S+"  // 해시태그 정규 표현식
        val matcher = Pattern.compile(hashTagPattern).matcher(text)

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_color)),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 텍스트 입력란에 텍스트를 설정
        binding.editTextTag.setText(spannable)
    }


    // 입력 검증 기능 설정
    private fun setupInputValidation() {
        // 제목 입력 감지
        binding.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // ViewModel에 제목 저장
                viewModel.setTitle(s?.toString() ?: "")
                checkInputValidation()
            }
        })

        // 내용 입력 감지
        binding.editTextContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setContent(s?.toString() ?: "")
                checkInputValidation()
            }
        })
    }

    // 입력 검증 확인 및 버튼 상태 업데이트
    private fun checkInputValidation() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()

        if (title.isNotEmpty() && content.isNotEmpty()) {
            binding.btn.isEnabled = true
            binding.btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btn.setBackgroundResource(R.drawable.bg_filled_selected)

            binding.btn.setOnClickListener {
                // ViewModel에 저장된 모든 데이터를 로그로 출력
                Log.d("PostContent", "=== 완료 버튼 클릭 시 저장된 데이터 ===")
                Log.d("PostContent", "출발일: ${viewModel.startDate.value}")
                Log.d("PostContent", "도착일: ${viewModel.endDate.value}")
                Log.d("PostContent", "선호 성별: ${viewModel.preferredGender.value}")
                Log.d("PostContent", "모집 인원: ${viewModel.recruitCapacity.value}")
                Log.d("PostContent", "모집 마감일: ${viewModel.recruitDeadline.value}")
                Log.d("PostContent", "최소 연령대: ${viewModel.preferredAgeMin.value}")
                Log.d("PostContent", "최대 연령대: ${viewModel.preferredAgeMax.value}")
                Log.d("PostContent", "최소 예산: ${viewModel.budgetMin.value}")
                Log.d("PostContent", "최대 예산: ${viewModel.budgetMax.value}")
                Log.d("PostContent", "제목: ${viewModel.title.value}")
                Log.d("PostContent", "내용: ${viewModel.content.value}")
                Log.d("PostContent", "해시태그: ${viewModel.tags.value?.joinToString(", ") ?: ""}")
                Log.d("PostContent", "이미지 URI: ${viewModel.photoUrls.value?.joinToString(",")}")
                Log.d("PostContent", "=====================================")

                // 서버에 게시글 등록 요청
                createPost()
            }
        } else {
            // 제목 또는 내용이 비어있는 경우
            binding.btn.isEnabled = false
            binding.btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.sub_gray))
            binding.btn.setBackgroundResource(R.drawable.bg_border_defualt)
        }
    }

    // 서버에 게시글 등록 요청하는 함수
    private fun createPost() {
        try {
            Log.d("PostContent", "=== 서버 전송 시작 ===")
            
            // ViewModel에서 데이터 가져오기
            val destinationId = viewModel.destinationId.value ?: 0L
            val title = viewModel.title.value ?: ""
            val content = viewModel.content.value ?: ""
            val startDate = viewModel.startDate.value ?: ""
            val endDate = viewModel.endDate.value ?: ""
            val recruitCapacity = viewModel.recruitCapacity.value ?: 0
            val recruitDeadline = viewModel.recruitDeadline.value ?: ""
            val preferredGender = viewModel.preferredGender.value ?: ""
            val preferredAgeMin = viewModel.preferredAgeMin.value ?: ""
            val preferredAgeMax = viewModel.preferredAgeMax.value ?: ""
            val budgetMin = viewModel.budgetMin.value ?: 0
            val budgetMax = viewModel.budgetMax.value ?: 0
            val photoUrls = viewModel.photoUrls.value ?: arrayOf("")
            val tags = viewModel.tags.value?.drop(1)?.toTypedArray() ?: arrayOf("")

            // 전송할 데이터 로그 출력
            Log.d("PostContent", "전송할 데이터:")
            Log.d("PostContent", "destinationId: $destinationId")
            Log.d("PostContent", "title: $title")
            Log.d("PostContent", "content: $content")
            Log.d("PostContent", "startDate: $startDate")
            Log.d("PostContent", "endDate: $endDate")
            Log.d("PostContent", "recruitCapacity: $recruitCapacity")
            Log.d("PostContent", "recruitDeadline: $recruitDeadline")
            Log.d("PostContent", "preferredGender: $preferredGender")
            Log.d("PostContent", "preferredAgeMin: $preferredAgeMin")
            Log.d("PostContent", "preferredAgeMax: $preferredAgeMax")
            Log.d("PostContent", "budgetMin: $budgetMin")
            Log.d("PostContent", "budgetMax: $budgetMax")
            Log.d("PostContent", "photoUrls: ${photoUrls.joinToString(", ")}")
            Log.d("PostContent", "tags: ${tags.joinToString(", ")}")

            // PostCreateRequest 객체 생성
            val postRequest = PostCreateRequest(
                destinationId = destinationId,
                title = title,
                content = content,
                startDate = startDate,
                endDate = endDate,
                recruitCapacity = recruitCapacity,
                recruitDeadline = recruitDeadline,
                preferredGender = preferredGender,
                preferredAgeMin = preferredAgeMin,
                preferredAgeMax = preferredAgeMax,
                budgetMin = budgetMin,
                budgetMax = budgetMax,
                photoUrls = photoUrls,
                tags = tags
            )

            Log.d("PostContent", "PostCreateRequest 객체 생성 완료")

            // 네트워크 요청 실행 (Coroutine 사용)
            lifecycleScope.launch {
                try {
                    Log.d("PostContent", "서버 요청 시작...")
                    
                    val postService = NetworkModule.providePostCreateApi(requireContext())
                    val response = postService.postCreate(postRequest)
                    
                    Log.d("PostContent", "=== 서버 응답 성공 ===")
                    Log.d("PostContent", "응답 ID: ${response.id}")
                    Log.d("PostContent", "게시글이 성공적으로 등록되었습니다!")
                    
                    Toast.makeText(requireContext(), "게시글이 등록되었습니다!", Toast.LENGTH_SHORT).show()
                    
                } catch (e: Exception) {
                    Log.e("PostContent", "=== 서버 요청 실패 ===")
                    Log.e("PostContent", "오류 메시지: ${e.message}")
                    Log.e("PostContent", "오류 상세: ${e.toString()}")
                    
                    Toast.makeText(requireContext(), "게시글 등록에 실패했습니다: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

        } catch (e: Exception) {
            Log.e("PostContent", "createPost 함수 실행 중 오류: ${e.message}")
            Toast.makeText(requireContext(), "오류가 발생했습니다: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
