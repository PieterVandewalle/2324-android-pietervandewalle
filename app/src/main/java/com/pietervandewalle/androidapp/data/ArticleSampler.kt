package com.pietervandewalle.androidapp.data

import com.pietervandewalle.androidapp.model.Article
import java.util.Date

object ArticleSampler {
    private val sampleArticles = mutableListOf(
        Article(
            author = "techcrunch.com",
            title = "OpenAI co-founders Sam Altman and Greg Brockman to join Microsoft",
            content = "Sam Altman and Greg Brockman and many of their former OpenAI colleagues will join Microsoft, the software conglomerate’s chief said. \"We look forward to moving quickly to provide them with the resources needed for their success,\" he added.",
            date = Date(1700467439013),
            imageUrl = "https://techcrunch.com/wp-content/uploads/2019/10/Sam-Altman-OpenAI-Greg-Brockman-DSC00947.jpg?resize=1200,800",
            readMoreUrl = "https://legacy.machaao.com/_c/techcrunch/https-techcrunch-com-2023-11-20-openai-co-founders-sam-altman-and-greg-brockman-to-join-microsoft",
            tags = listOf("microsoft", "sam altman", "openai", "greg brockman", "ai", "techcrunch.com"),
        ),
        Article(
            author = "techcrunch.com",
            title = "Altman won’t return as OpenAI’s CEO after all",
            content = "openAI has appointed Emmett Shear, the co-founder of video streaming site Twitch, as interim CEO. The board's decision to remove Altman reportedly infuriated Satya Nadella, the CEO of a major OpenAI backer and partner.",
            date = Date(1700461438971),
            imageUrl = "https://techcrunch.com/wp-content/uploads/2023/01/Screen-Shot-2023-01-17-at-8.41.09-PM-e1674068012174.png?resize=1200,828",
            readMoreUrl = "https://legacy.machaao.com/_c/techcrunch/https-techcrunch-com-2023-11-19-altman-wont-return-as-openais-ceo-after-all",
            tags = listOf("altman", "openai", "ai", "startups", "techcrunch.com"),
        ),

        Article(
            author = "techcrunch.com",
            title = "OpenAI’s board is no match for investors’ wrath",
            content = "The board of openAI removed the company's CEO, Sam Altman. But it seems that investors and partners — and many of its employees — were more comfortable with the idea of the board’s power than it exercising that power. The board unceremoniously announced that Altman would be replaced by Mira Murati, openAI’s CTO, on a temporary basis.",
            date = Date(1700418239017),
            imageUrl = "https://techcrunch.com/wp-content/uploads/2019/07/MSFT-Nadella-OpenAI-Altman-09-official-joint-pic.jpg?resize=1200,764",
            readMoreUrl = "https://legacy.machaao.com/_c/techcrunch/https-techcrunch-com-2023-11-19-openais-board-is-no-match-for-investors-wrath",
            tags = listOf("openai", "ai", "startups", "venture", "techcrunch.com"),
        ),
        Article(
            author = "techcrunch.com",
            title = "OpenAI’s board is no match for investors’ wrath",
            content = "The board of directors removed openAI's CEO, Sam Altman, on a temporary basis. Many investors and partners — and many of its employees — were more comfortable with the idea of the board’s power than it exercising that power. But many in the tech community felt the opposite. The board considers its next move, openAI top AI researchers and executives are calling it quits.",
            date = Date(1700355838859),
            imageUrl = "https://techcrunch.com/wp-content/uploads/2019/07/MSFT-Nadella-OpenAI-Altman-09-official-joint-pic.jpg?resize=1200,764",
            readMoreUrl = "https://legacy.machaao.com/_c/techcrunch/https-techcrunch-com-2023-11-18-openais-board-is-no-match-for-investors-wrath",
            tags = listOf("openai", "ai", "startups", "venture", "techcrunch.com"),
        ),
        Article(
            author = "techcrunch.com",
            title = "A timeline of Sam Altman’s firing from OpenAI — and the fallout",
            content = "Ex-Y Combinator president Sam Altman was fired as CEO of openai. The company's longtime president and co-founder, Greg Brockman, resigned. He says he received a text from Mira Murati about scheduling a call.",
            date = Date(1700338739153),
            imageUrl = "https://techcrunch.com/wp-content/uploads/2023/04/openai-getty.jpg?resize=1200,800",
            readMoreUrl = "https://legacy.machaao.com/_c/techcrunch/https-techcrunch-com-2023-11-18-a-timeline-of-sam-altmans-firing-from-openai-and-the-fallout",
            tags = listOf("sam altman", "openai", "ai", "techcrunch.com"),
        ),
        Article(
            author = "techcrunch.com",
            title = "Deal Dive: An AI application that isn’t just marginally better",
            content = "Since the AI frenzy started over a year ago, we’ve seen many, um, interesting use cases for the tech that’s been deemed the greatest innovation since the internet. From AI meant to help sales folks be 5% faster, to AI bots that teach you to understand your human relationships to AI that writes for you — just not 100% accurately.",
            date = Date(1700327038978),
            imageUrl = "https://techcrunch.com/wp-content/uploads/2023/11/GettyImages-507492447.jpg?resize=1200,900",
            readMoreUrl = "https://legacy.machaao.com/_c/techcrunch/https-techcrunch-com-2023-11-18-ai-real-estate-pippin-title",
            tags = listOf("ai", "fintech", "startups", "techcrunch.com"),
        ),
    )

    val getAll: () -> MutableList<Article> = {
        sampleArticles
    }
}
