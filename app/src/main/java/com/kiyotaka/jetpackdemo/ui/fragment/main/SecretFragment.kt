package com.kiyotaka.jetpackdemo.ui.fragment.main

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.FragmentSecretBinding
import com.kiyotaka.jetpackdemo.model.CustomViewModelProvider
import com.kiyotaka.jetpackdemo.model.MediaViewModel
import com.kiyotaka.jetpackdemo.model.SecretViewModel
import kotlinx.android.synthetic.main.fragment_secret.*


class SecretFragment : Fragment() {

    private val secretViewModel: SecretViewModel by viewModels {
        CustomViewModelProvider.providerSecretModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding :FragmentSecretBinding= DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_secret,
            container,
            false
        )
        initData(binding)
        onSubscribeUI(binding)
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initData(binding :FragmentSecretBinding) {
        binding.lifecycleOwner = this
        binding.model = secretViewModel
    }

    private fun onSubscribeUI(binding: FragmentSecretBinding){
        binding.tvSecretCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvSecretContent.text = showSecret()
    }

    private fun showSecret():SpannableStringBuilder {
        val styleSpan = SpannableStringBuilder()
        var count = 0
        val s1 = "  欢迎您注册使用由广州博微智能科技有限公司开发与运营的心理咨询服务平台，请您仔细阅读以下条款，并确定您已完全理解本协议内容。\n" +
                "  若您勾选本协议并且提交资料，则视为您签署本协议，请您仔细阅读本协议，本协议将构成您与本公司之间具有约束力的法律文件。\n"
        styleSpan.append(s1)
        count += s1.length

        val s2 = "一.\t用户个人信息\n"
        styleSpan.append(s2)
        styleSpan.setSpan(StyleSpan(Typeface.BOLD), count, count + s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        styleSpan.setSpan(RelativeSizeSpan(1.5f), count, count + s2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        count += s2.length

        val s3 = "1.1在您注册心理咨询app后，您将成为心理咨询app的用户，用户必须保证注册信息的真实性与合法性并承担全部法律责任。\n" +
                "1.2您不得以任何形式转让或授权他人使用自己的账户，也不能盗用他人账户，以上行为所产生的法律后果由用户自行承担。\n" +
                "1.3用户注册并且通过咨询师审核方可使用本软件成为正式用户，可获得“心理咨询app”规定用户所应享受的一切权限。\n"
        styleSpan.append(s3)
        count += s3.length

        val s4 = "二.\t服务使用\n"
        styleSpan.append(s4)
        styleSpan.setSpan(StyleSpan(Typeface.BOLD), count, count + s4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        styleSpan.setSpan(RelativeSizeSpan(1.5f), count, count + s4.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        count += s4.length

        val s5 = "2.1“心理咨询app”为心理咨询师提供与用户交流的平台，具体服务由本公司根据实际情况提供。\n" +
                "2.2“心理咨询app”仅提供心理咨询相关的网络服务，除此之外与相关网络服务有关的设备（例如电脑，手机及其他接入互联网或移动有关的设备）及其需的费用（如为接入互联网而支付的电话费及上网费、为使用移动网而支付的手机费、线下咨询的交通费等）均由用户自己承担。\n"
        styleSpan.append(s5)
        count += s5.length

        val s6 = "三.\t使用规则\n"
        styleSpan.append(s6)
        styleSpan.setSpan(StyleSpan(Typeface.BOLD), count, count + s6.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        styleSpan.setSpan(RelativeSizeSpan(1.5f), count, count + s6.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        count += s6.length

        val s7 = "3.1用户应该遵守中华人民共和国相关法律，包括但不限于《中华人民共和国计算机信息系统安全保护条例》、《计算机软件保护条例》、《最高人民法院关于审理涉及计算机网络著作权纠纷案适用法律若干问题的解释（法释【2004】1号）》、《全国人大常委会关于维护互联网安全的决定》、《互联网电子公告服务管理规定》、《互联网著作权行政保护办法》和《信息网络传播权保护条例》等有关计算机互联网规定和知识产权的法律和法规。\n" +
                "3.2用户对其自行发表的言论和观点负责，均不能代表本公司赞同您的观点或证实其观点和言论的真实性。\n"
        styleSpan.append(s7)
        count += s7.length

        val s8 = "三.\t用户隐私\n"
        styleSpan.append(s8)
        styleSpan.setSpan(StyleSpan(Typeface.BOLD), count, count + s8.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        styleSpan.setSpan(RelativeSizeSpan(1.5f), count, count + s8.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        count += s8.length

        val s9 = "4.1本公司在未获得用户授权情况下，不会对外提供或公开用户注册资料及用户在“心理咨询app”平台上非公开的内容，有关法律法规要求除外。\n" +
                "4.2在不透露用户隐私的情况下，本公司有权对用户数据库进行分析并对用户数据库进行商业使用。\n" +
                "4.3用户应当保密咨询者的隐私，在未获得咨询者的授权许可下，不应当向外透露咨询者的隐私情况，若因此产生的法律责任应由用户承担。\n"
        styleSpan.append(s9)
        count += s9.length

        val s10 = "三.\t其他\n"
        styleSpan.append(s10)
        styleSpan.setSpan(StyleSpan(Typeface.BOLD), count, count + s10.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        styleSpan.setSpan(RelativeSizeSpan(1.5f), count, count + s10.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        count += s10.length

        val s11 = "5.1本协议的订立，执行和解释及争议的解决均适用中华人民共和国法律。\n" +
                "5.2如本协议中的任何条款无论因何种因素完全或者部分无效或者不具有执行力，本协议其他条款仍因具有约束力。\n" +
                "5.3本协议最终解释权及修订权归广州博微智能科技有限公司所有。\n\n\n\n\n"
        styleSpan.append(s11)

        return styleSpan
    }

}
