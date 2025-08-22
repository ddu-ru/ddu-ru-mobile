package com.gildongmu.ddu_ru_mobile.ui.post

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gildongmu.ddu_ru_mobile.R
import com.gildongmu.ddu_ru_mobile.databinding.ActivityPostContentBinding
import java.util.regex.Pattern

class PostContentActivity : AppCompatActivity() {

    private lateinit var postContentBinding: ActivityPostContentBinding
    private val tagsList: MutableList<String> = mutableListOf()  // 해시태그를 저장할 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postContentBinding = ActivityPostContentBinding.inflate(layoutInflater)
        setContentView(postContentBinding.root)

        setupHashtagFunctionality()
    }

    private fun setupHashtagFunctionality() {
        // Focus 되었을 때 자동으로 # 추가
        postContentBinding.editTextTag.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && postContentBinding.editTextTag.text.isNullOrEmpty()) {
                postContentBinding.editTextTag.setText("#")
            }
        }

        postContentBinding.editTextTag.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s.toString()
                // 공백이 입력된 경우, 새로운 # 추가
                if (currentText.endsWith(" ") && !currentText.trim().endsWith("#")) {
                    val newText = currentText.trim() + "  #"  // 공백 뒤에 새로운 # 추가
                    postContentBinding.editTextTag.setText(newText)  // 텍스트 필드에 업데이트
                    updateTextWithColor(newText)  // 해시태그 색상 업데이트
                    postContentBinding.editTextTag.setSelection(newText.length)  // 커서를 끝으로 이동
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        postContentBinding.editTextTag.setOnKeyListener { view, keyCode, event ->
            //삭제를 눌렀을 때
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                val currentText = postContentBinding.editTextTag.text.toString()
                val cursorPosition = postContentBinding.editTextTag.selectionStart

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
                    postContentBinding.editTextTag.setText(newText)  // 텍스트 필드에 업데이트
                    updateTextWithColor(newText)
                    postContentBinding.editTextTag.setSelection(newText.length)  // 커서를 끝으로 이동
                    return@setOnKeyListener true  // 이벤트 처리 완료
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnKeyListener true  // true를 반환하여 기본 동작을 막음
            }
            false  // 다른 키는 기본 처리
        }

        // 입력 완료 후 해시태그 색상 변경
        postContentBinding.editTextTag.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val currentText = postContentBinding.editTextTag.text.toString().trim()
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
        val hashTagPattern = "#\\w+"  // 해시태그 정규 표현식
        val matcher = Pattern.compile(hashTagPattern).matcher(text)

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this, R.color.main_color)),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 텍스트 입력란에 텍스트를 설정
        postContentBinding.editTextTag.setText(spannable)
    }
}
