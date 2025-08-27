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
import com.gildongmu.ddu_ru_mobile.R
import com.gildongmu.ddu_ru_mobile.databinding.ActivityPostContentBinding
import java.util.regex.Pattern

class PostContentFragment : Fragment() {
    val REQUEST_CODE_PERMISSIONS = 1001
    private var _binding: ActivityPostContentBinding? = null
    private val binding get() = _binding!!
    private val tagsList: MutableList<String> = mutableListOf()  // 해시태그를 저장할 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityPostContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupHashtagFunctionality()
        setupInputValidation()  // 입력 검증 기능 추가

        binding.imageViewButton.setOnClickListener {
            getGalleryPermission()
        }

        binding.deleteButton.setOnClickListener {
            val imageView: ImageView = binding.imageViewButton
            imageView.setImageURI(null)  // 이미지를 지우기
            binding.deleteButton.visibility = View.GONE  // 삭제 버튼 숨기기
        }
    }

    // 갤러리 선택 결과 처리 등록을 클래스 레벨로 이동
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val imageView: ImageView = binding.imageViewButton
            imageView.setImageURI(it)  // 이미지 선택 후 이미지 뷰에 URI 설정
            binding.deleteButton.visibility = View.VISIBLE
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
                val currentText = s.toString()

                // 공백이 입력된 경우, 새로운 # 추가
                if (currentText.endsWith(" ") && !currentText.trim().endsWith("#")) {
                    val newText = currentText.trim() + "  #"  // 공백 뒤에 새로운 # 추가
                    binding.editTextTag.setText(newText)  // 텍스트 필드에 업데이트
                    updateTextWithColor(newText)  // 해시태그 색상 업데이트
                    binding.editTextTag.setSelection(newText.length)  // 커서를 끝으로 이동
                }
            }

            override fun afterTextChanged(s: Editable?) {}
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
            else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnKeyListener true  // true를 반환하여 기본 동작을 막음
            }
            false  // 다른 키는 기본 처리
        }

        // 입력 완료 후 해시태그 색상 변경
        binding.editTextTag.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val currentText = binding.editTextTag.text.toString().trim()
                if (currentText.isNotEmpty() && currentText.startsWith("#")) {
                    tagsList.add(currentText)
                    updateTextWithColor(currentText)  // 해시태그 색상 업데이트
                }
                true
            } else {
                false
            }
        }
    }

    // 텍스트를 색상별로 구분하여 표시하는 함수
    private fun updateTextWithColor(text: String) {
        val spannable = SpannableString(text)

        // 주황색 해시태그 부분
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
                checkInputValidation()
            }
        })

        // 내용 입력 감지
        binding.editTextContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                checkInputValidation()
            }
        })
    }

    // 입력 검증 확인 및 버튼 상태 업데이트
    private fun checkInputValidation() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()

        if (title.isNotEmpty() && content.isNotEmpty()) {
            // 제목과 내용이 모두 입력된 경우
            binding.btn.isEnabled = true
            binding.btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btn.setBackgroundResource(R.drawable.bg_filled_selected)
        } else {
            // 제목 또는 내용이 비어있는 경우
            binding.btn.isEnabled = false
            binding.btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.sub_gray))
            binding.btn.setBackgroundResource(R.drawable.bg_border_defualt)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
